package com.alevya.authsber.service;

import com.alevya.authsber.dto.OrderDtoRequest;
import com.alevya.authsber.dto.OrderDtoResponse;
import com.alevya.authsber.exception.BadRequestException;
import com.alevya.authsber.exception.NotFoundException;
import com.alevya.authsber.model.Order;
import com.alevya.authsber.model.Slot;
import com.alevya.authsber.model.Workplace;
import com.alevya.authsber.repository.OrderRepository;
import com.alevya.authsber.repository.ServiceRepository;
import com.alevya.authsber.repository.WorkplaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final WorkplaceRepository workplaceRepository;
    private final ServiceRepository serviceRepository;

    public OrderService(OrderRepository orderRepository,
                        WorkplaceRepository workplaceRepository,
                        ServiceRepository serviceRepository) {
        this.orderRepository = orderRepository;
        this.workplaceRepository = workplaceRepository;
        this.serviceRepository = serviceRepository;
    }

    public OrderDtoResponse createOrder(OrderDtoRequest dto) {
        if (dto == null) {
            throw new BadRequestException("Invalid order");
        }
        return mapToOrderDto(orderRepository.save(
                mapToOrder(dto)));
    }

    public OrderDtoResponse createOrderBySlot(Slot slot, Long id) {
        if (slot == null) {
            throw new BadRequestException("Invalid slot");
        }
        if (slot.getWorkplaceId() == null) {
            throw new BadRequestException("Invalid WorkplaceId");
        }
        if (orderRepository.findOrderByAllParam(slot.getWorkplaceId(), slot.getDate(), slot.getTimeStart()).size() > 0) {
            throw new BadRequestException("Order exists at this time");
        }
        Workplace workplace = workplaceRepository.findById(slot.getWorkplaceId()).orElseThrow(()
                -> new NotFoundException("Workplace not found!"));
        com.alevya.authsber.model.Service service = serviceRepository.findById(slot.getServiceId()).orElseThrow(()
                -> new NotFoundException("Workplace not found!"));
        Set<com.alevya.authsber.model.Service> services = new HashSet<>();
        services.add(service);

        return mapToOrderDto(
                orderRepository.save(
                        Order.builder()
                                .date(slot.getDate())
                                .timeStart(slot.getTimeStart())
                                .timeFinish(slot.getTimeFinish())
                                .workTimeId(slot.getWorkTimeId())
                                .workplace(workplace)
                                .serviceId(slot.getServiceId())
                                .comment(slot.getComment())
                                .clientId(id)
                                .build()
                )
        );
    }

    public OrderDtoResponse getOrderById(Long id) {
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        return mapToOrderDto(orderRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Order not found!")));
    }

    public List<OrderDtoResponse> getOrderByUserId(Long id) {
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        return orderRepository.findAllByClientId(id).stream()
                .map(this::mapToOrderDto)
                .collect(Collectors.toList());
    }

    public List<OrderDtoResponse> getOrderByWorktimeId(Long id) {
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        return orderRepository.findAllByWorktimeId(id).stream()
                .map(this::mapToOrderDto)
                .collect(Collectors.toList());
    }

    public List<OrderDtoResponse> getOrderByWorkplaceId(Long id) {
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        return orderRepository.findAllByWorkplaceId(id).stream()
                .map(this::mapToOrderDto)
                .collect(Collectors.toList());
    }

    public List<OrderDtoResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToOrderDto)
                .collect(Collectors.toList());
    }

    public OrderDtoResponse updateOrder(Long id, OrderDtoRequest orderDtoRequest) {
        if (orderDtoRequest == null) {
            throw new BadRequestException("Invalid order");
        }
        Order oldOrder = orderRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Order not found!"));
        if (orderDtoRequest.getDate() != null) {
            oldOrder.setDate(orderDtoRequest.getDate());
        }
        if (orderDtoRequest.getTimeStart() != null) {
            oldOrder.setTimeStart(orderDtoRequest.getTimeStart());
        }
        if (orderDtoRequest.getTimeFinish() != null) {
            oldOrder.setTimeFinish(orderDtoRequest.getTimeFinish());
        }
        if (orderDtoRequest.getWorkTimeId() != null) {
            oldOrder.setWorkTimeId(orderDtoRequest.getWorkTimeId());
        }
        if (orderDtoRequest.getWorkplaceId() != null) {
            Workplace workplace = workplaceRepository.findById(orderDtoRequest.getWorkplaceId()).orElseThrow();
            oldOrder.setWorkplace(workplace);
        }
        if (orderDtoRequest.getComment() != null) {
            oldOrder.setComment(orderDtoRequest.getComment());
        }
        if (orderDtoRequest.getClientId() != null) {
            oldOrder.setClientId(orderDtoRequest.getClientId());
        }
        return mapToOrderDto(orderRepository.saveAndFlush(oldOrder));

    }

    public void deleteOrder(Long id) {
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        orderRepository.deleteById(id);
    }

    public OrderDtoResponse mapToOrderDto(Order order) {
        OrderDtoResponse orderDtoResponse = OrderDtoResponse.builder()
                .id(order.getId())
                .date(order.getDate())
                .timeStart(order.getTimeStart())
                .timeFinish(order.getTimeFinish())
                .workTimeId(order.getWorkTimeId())
                .workplaceId(order.getWorkplace().getId())
                .serviceId(order.getServiceId())
                .comment(order.getComment())
                .clientId(order.getClientId())
                .build();
        return orderDtoResponse;
    }

    public Order mapToOrder(OrderDtoRequest dto) {
        Order order = new Order();
        order.setDate(dto.getDate());
        order.setTimeStart(dto.getTimeStart());
        order.setTimeFinish(dto.getTimeFinish());
        order.setWorkTimeId(dto.getWorkTimeId());
        Workplace workplace = workplaceRepository.findById(dto.getWorkplaceId()).orElseThrow();
        order.setWorkplace(workplace);
        order.setServiceId(dto.getServiceId());
        order.setComment(dto.getComment());
        order.setClientId(dto.getClientId());

        return order;
    }

    public Page<OrderDtoResponse> findAllOrdersPageable(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        return new PageImpl<>(page.stream().map(this::mapToOrderDto)
                .collect(Collectors.toList()), page.getPageable(), page.getTotalElements());
    }
}
