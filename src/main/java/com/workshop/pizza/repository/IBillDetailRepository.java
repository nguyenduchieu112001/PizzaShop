package com.workshop.pizza.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.workshop.pizza.entity.BillDetail;

public interface IBillDetailRepository extends JpaRepository<BillDetail, Integer> {

	@Query(value = "select sum(bd.quantity * ps.product_price) from bill b join bill_detail bd on b.id = bd.bill_id join product_size ps on ps.id = bd.product_size_id where b.id = ?1 group by b.id;", nativeQuery = true)
	long subtotalBill(int billId);

	List<BillDetail> findByBillId(int billId);

}
