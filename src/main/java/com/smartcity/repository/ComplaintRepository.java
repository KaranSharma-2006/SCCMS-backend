package com.smartcity.repository;

import com.smartcity.entity.Complaint;
import com.smartcity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByUserOrderByCreatedAtDesc(User user);

    List<Complaint> findAllByOrderByCreatedAtDesc();
}
