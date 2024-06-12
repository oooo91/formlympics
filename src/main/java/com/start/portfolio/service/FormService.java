package com.start.portfolio.service;

import com.start.portfolio.dto.AlarmDto;
import com.start.portfolio.dto.AlarmDto.Response;
import com.start.portfolio.dto.FormDto;
import com.start.portfolio.dto.OrdersDto;
import com.start.portfolio.dto.ProductDto;
import com.start.portfolio.entity.Alarm;
import com.start.portfolio.entity.Cart;
import com.start.portfolio.entity.Form;
import com.start.portfolio.entity.Orders;
import com.start.portfolio.entity.Product;
import com.start.portfolio.entity.User;
import com.start.portfolio.entity.args.AlarmArgs;
import com.start.portfolio.enums.OrderStatus;
import com.start.portfolio.kafka.dto.AlarmEvent;
import com.start.portfolio.kafka.enums.AlarmTopic;
import com.start.portfolio.kafka.producer.AlarmProducer;
import com.start.portfolio.repository.AlarmRepository;
import com.start.portfolio.repository.CartRepository;
import com.start.portfolio.repository.FormRepository;
import com.start.portfolio.repository.OrdersRepository;
import com.start.portfolio.repository.ProductRepository;
import com.start.portfolio.repository.UserRepository;
import com.start.portfolio.util.aop.LogAroundLogin;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FormService {

	private final UserRepository userRepository;
	private final FormRepository formRepository;
	private final ProductRepository productRepository;
	private final OrdersRepository ordersRepository;
	private final AlarmRepository alarmRepository;
	private final CartRepository cartRepository;
	private final OrderExceptionLogService orderExceptionLogService;
	private final StockService stockService;
	private final AlarmProducer alarmProducer;

	@Transactional
	public void saveForm(Long userId, FormDto.Request request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("사용자가 없습니다."));

		Form form = request.toEntity();
		form.setUser(user);
		Form savedForm = formRepository.save(form);

		List<Product> productList = request.productList().stream()
			.map(dto -> Product.builder()
				.form(savedForm)
				.productName(dto.productName())
				.stock(dto.stock())
				.price(dto.price())
				.build())
			.toList(); // toList() -> 불변한 list 생성, 그게 아닐 경우엔 .collect(Collectors.toList()) 사용

		productRepository.saveAll(productList);
	}


	@Transactional(readOnly = true)
	public FormDto.Response getForm(Long formId) {
		FormDto.Response response = formRepository.findById(formId)
			.orElseThrow(() -> new RuntimeException("삭제된 폼입니다.")).toDto();
		List<Product> productList = productRepository.findAllByFormId(formId);

		List<ProductDto.Response> productDtoList = productList.stream()
			.map(Product::toDto).toList();
		response.setProductList(productDtoList);
		return response;
	}

	@Transactional
	public void modifyForm(Long userId, Long formId, FormDto.Request request) {

		Form form = formRepository.findById(formId)
			.orElseThrow(() -> new RuntimeException("삭제된 폼입니다."));

		if (!Objects.equals(form.getUser().getId(), userId)) {
			throw new RuntimeException("권한이 없습니다.");
		}
		form.update(request);
	}

	@Transactional
	@LogAroundLogin
	public void order(Long userId, List<OrdersDto.Request> requests) {

		try {
			// TODO 주문 내역 저장 및 재고 감소
			User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("사용자가 없습니다."));

			List<Orders> ordersList = requests.stream()
				.map(dto -> {
					Product product = productRepository.findById(dto.productId())
						.orElseThrow(() -> new RuntimeException("상품이 없습니다."));
					stockService.decrease(product.getId(), dto.quantity());

					return Orders.builder()
						.totalPrice(product.getPrice() * dto.quantity())
						.depositName(dto.depositName())
						.quantity(dto.quantity())
						.orderStatus(OrderStatus.CREATED)
						.product(product)
						.user(user)
						.build();
				}).toList();

			ordersRepository.saveAll(ordersList);
		} catch (RuntimeException e) {
			orderExceptionLogService.saveLog(userId, e.getMessage());
		}

	}

	// TODO user, form 과 강한 결합 -> 분리 필요
	@Transactional
	public void like(Long userId, Long formId, AlarmDto.Request request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("사용자가 없습니다."));

		Form form = formRepository.findById(formId)
			.orElseThrow(() -> new RuntimeException("삭제된 폼입니다."));

		// TODO 이미 좋아요를 누른 경우 -> 장바구니 삭제 / 그렇지 않은 경우 -> 장바구니 담기 + 폼 좋아요 증가 + 알람 이벤트 생성
		cartRepository.findByUserIdAndFormId(userId, formId).ifPresentOrElse(
			cart -> {
				cartRepository.delete(cart);
				form.decreaseLike();
				formRepository.save(form);
			},
			() -> {
				// TODO 사용자의 장바구니 담기
				cartRepository.save(Cart.builder()
					.user(user)
					.form(form)
					.build());

				// TODO 폼 좋아요 +1 증가
				form.increaseLike();
				formRepository.save(form);

				// TODO 알람 이벤트 생성 -> 비동기
				alarmProducer.send(
					new AlarmEvent(
						form.getUser().getId(),
						request.alarmType(),
						new AlarmArgs(user.getId(), user.getName(), formId, form.getTitle()))
				);
			}
		);
	}

	@Transactional
	public List<Response> alarmList(Long userId) {
		return alarmRepository.findAllByUserId(userId).stream()
			.map(Alarm::toDto).toList();
	}
}
