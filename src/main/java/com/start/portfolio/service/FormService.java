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
import com.start.portfolio.enums.AlarmType;
import com.start.portfolio.enums.OrderStatus;
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

	@Transactional
	public void alarm(Long userId, Long formId, AlarmDto.Request request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("사용자가 없습니다."));

		Form form = formRepository.findById(formId)
			.orElseThrow(() -> new RuntimeException("삭제된 폼입니다."));

		// TODO 좋아요 cart 에 담기
		cartRepository.findByUserIdAndFormId(userId, formId).ifPresentOrElse(
			cartRepository::delete,
			() -> {
				cartRepository.save(Cart.builder()
					.user(user)
					.form(form)
					.build());

				// TODO 알람 저장하기
				alarmRepository.save(
					Alarm.builder()
						.registeredAt(request.registeredAt())
						.user(form.getUser())
						.alarmType(request.alarmType())
						.alarmArgs(AlarmArgs.builder()
							.fromUserId(user.getId())
							.formId(formId)
							.build())
						.build());
			}
		);
	}

	@Transactional
	public List<Response> alarmList(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("사용자가 없습니다."));

		return alarmRepository.findAllByUser(user).stream()
			.map(Alarm::toDto).toList();

	}
}
