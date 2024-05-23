package com.start.portfolio.service.order;

import com.start.portfolio.dto.order.FormDto;
import com.start.portfolio.dto.order.FormDto.Response;
import com.start.portfolio.dto.order.ProductDto;
import com.start.portfolio.entity.order.Form;
import com.start.portfolio.entity.order.Product;
import com.start.portfolio.entity.order.User;
import com.start.portfolio.repository.order.FormRepository;
import com.start.portfolio.repository.order.ProductRepository;
import com.start.portfolio.repository.order.UserRepository;
import java.util.List;
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

	@Transactional
	public void saveForm(Long userId, FormDto.Request request) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("사용자가 없습니다."));

		Form form = request.toFormEntity();
		form.setUser(user);

		Form savedForm = formRepository.save(form);

		List<Product> productList = request.productList().stream()
			.map(dto -> Product.builder()
				.form(savedForm)
				.productName(dto.productName())
				.stock(dto.stock())
				.price(dto.price())
				.build())
			.toList();

		productRepository.saveAll(productList);
	}


	@Transactional
	public FormDto.Response getForm(Long formId) {
		Form form = formRepository.findById(formId)
			.orElseThrow(() -> new RuntimeException("삭제된 폼입니다."));

		List<Product> productList = productRepository.findAllByFormId(form.getId());

		List<ProductDto.Response> productDtoList = productList.stream()
			.map(product -> ProductDto.Response.builder()
				.productId(product.getId())
				.productName(product.getProductName())
				.stock(product.getStock())
				.price(product.getPrice())
				.build()).toList();

		return FormDto.Response.builder()
			.title(form.getTitle())
			.content(form.getContent())
			.likes(form.getLikes())
			.depositStartDate(form.getDepositStartDate())
			.depositEndDate(form.getDepositEndDate())
			.productList(productDtoList)
			.build();
	}
}
