package com.bookwheelapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bookwheelapp.entities.MyOrder;

public interface MyOrderRepo extends JpaRepository<MyOrder, Long>{

	@Query("select o from MyOrder o where o.orderId= :orderId")
	public MyOrder findByOrderId(String orderId); 
}
