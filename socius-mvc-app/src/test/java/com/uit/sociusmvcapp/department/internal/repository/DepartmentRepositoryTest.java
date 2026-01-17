package com.uit.sociusmvcapp.department.internal.repository;

import com.uit.sociusmvcapp.department.dto.request.SearchDepartmentRequest;
import com.uit.sociusmvcapp.department.internal.domain.Department;
import com.uit.sociusmvcapp.department.internal.persistence.DepartmentMapper;
import com.uit.sociusmvcapp.shared.request.SortRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for DepartmentRepository using H2 database. Tests all repository methods with
 * success scenarios.
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class DepartmentRepositoryTest {

  @Autowired private DepartmentMapper departmentMapper;

  @Autowired private JdbcTemplate jdbcTemplate;

  private Department testDepartment;

  @BeforeEach
  void setUp() {
    // Clean database before each test
    jdbcTemplate.execute("DELETE FROM departments");

    // Reset auto-increment
    jdbcTemplate.execute("ALTER TABLE departments ALTER COLUMN id RESTART WITH 1");

    // Create test department
    testDepartment =
        Department.builder().departmentCode("IT").departmentName("Information Technology").build();
  }

  @Test
  void findByDepartmentCode_SUCCESS1() {
    // Given
    departmentMapper.create(testDepartment);

    // When
    Department result = departmentMapper.findByDepartmentCode("IT");

    // Then
    org.junit.jupiter.api.Assertions.assertNotNull(result);
    org.junit.jupiter.api.Assertions.assertEquals("IT", result.getDepartmentCode());
    org.junit.jupiter.api.Assertions.assertEquals(
        "Information Technology", result.getDepartmentName());
  }

  @Test
  void findByDepartmentCode_SUCCESS2() {
    // Given - no department exists

    // When
    Department result = departmentMapper.findByDepartmentCode("NONEXISTENT");

    // Then
    org.junit.jupiter.api.Assertions.assertNull(result);
  }

  @Test
  void create_SUCCESS1() {
    // Given
    Department newDepartment =
        Department.builder().departmentCode("HR").departmentName("Human Resources").build();

    // When
    departmentMapper.create(newDepartment);

    // Then
    Department result = departmentMapper.findByDepartmentCode("HR");
    org.junit.jupiter.api.Assertions.assertNotNull(result);
    org.junit.jupiter.api.Assertions.assertEquals("HR", result.getDepartmentCode());
    org.junit.jupiter.api.Assertions.assertEquals("Human Resources", result.getDepartmentName());
  }

  @Test
  void update_SUCCESS1() {
    // Given
    departmentMapper.create(testDepartment);
    Department updateDepartment =
        Department.builder().departmentCode("IT").departmentName("IT Department").build();

    // When
    departmentMapper.update(updateDepartment);

    // Then
    Department result = departmentMapper.findByDepartmentCode("IT");
    org.junit.jupiter.api.Assertions.assertNotNull(result);
    org.junit.jupiter.api.Assertions.assertEquals("IT", result.getDepartmentCode());
    org.junit.jupiter.api.Assertions.assertEquals("IT Department", result.getDepartmentName());
  }

  @Test
  void deactivate_SUCCESS1() {
    // Given
    departmentMapper.create(testDepartment);

    // When
    departmentMapper.deactivate("IT");

    // Then
    Department result = departmentMapper.findByDepartmentCode("IT");
    org.junit.jupiter.api.Assertions.assertNull(result);

    Department deletedResult = departmentMapper.findDeletedByDepartmentCode("IT");
    org.junit.jupiter.api.Assertions.assertNotNull(deletedResult);
    org.junit.jupiter.api.Assertions.assertEquals("IT", deletedResult.getDepartmentCode());
  }

  @Test
  void activate_SUCCESS1() {
    // Given - create and then deactivate
    departmentMapper.create(testDepartment);
    departmentMapper.deactivate("IT");

    // When
    departmentMapper.activate("IT", "Information Technology Updated");

    // Then
    Department result = departmentMapper.findByDepartmentCode("IT");
    org.junit.jupiter.api.Assertions.assertNotNull(result);
    org.junit.jupiter.api.Assertions.assertEquals("IT", result.getDepartmentCode());
    org.junit.jupiter.api.Assertions.assertEquals(
        "Information Technology Updated", result.getDepartmentName());
  }

  @Test
  void findDeletedByDepartmentCode_SUCCESS1() {
    // Given
    departmentMapper.create(testDepartment);
    departmentMapper.deactivate("IT");

    // When
    Department result = departmentMapper.findDeletedByDepartmentCode("IT");

    // Then
    org.junit.jupiter.api.Assertions.assertNotNull(result);
    org.junit.jupiter.api.Assertions.assertEquals("IT", result.getDepartmentCode());
    org.junit.jupiter.api.Assertions.assertEquals(
        "Information Technology", result.getDepartmentName());
  }

  @Test
  void findDeletedByDepartmentCode_SUCCESS2() {
    // Given - active department (not deleted)
    departmentMapper.create(testDepartment);

    // When
    Department result = departmentMapper.findDeletedByDepartmentCode("IT");

    // Then
    org.junit.jupiter.api.Assertions.assertNull(result);
  }

  @Test
  void existsByDepartmentCode_SUCCESS1() {
    // Given
    departmentMapper.create(testDepartment);

    // When
    boolean result = departmentMapper.existsByDepartmentCode("IT");

    // Then
    org.junit.jupiter.api.Assertions.assertTrue(result);
  }

  @Test
  void existsByDepartmentCode_SUCCESS2() {
    // Given - no department exists

    // When
    boolean result = departmentMapper.existsByDepartmentCode("NONEXISTENT");

    // Then
    org.junit.jupiter.api.Assertions.assertFalse(result);
  }

  @Test
  void search_SUCCESS1() {
    // Given
    departmentMapper.create(testDepartment);

    Department hrDepartment =
        Department.builder().departmentCode("HR").departmentName("Human Resources").build();
    departmentMapper.create(hrDepartment);

    SearchDepartmentRequest criteria = new SearchDepartmentRequest();
    criteria.setDepartmentCode("IT");

    // When
    List<Department> results = departmentMapper.search(criteria, Collections.emptyList(), 10, 0);

    // Then
    org.junit.jupiter.api.Assertions.assertNotNull(results);
    org.junit.jupiter.api.Assertions.assertEquals(1, results.size());
    org.junit.jupiter.api.Assertions.assertEquals("IT", results.get(0).getDepartmentCode());
  }

  @Test
  void search_SUCCESS2() {
    // Given
    departmentMapper.create(testDepartment);

    Department hrDepartment =
        Department.builder().departmentCode("HR").departmentName("Human Resources").build();
    departmentMapper.create(hrDepartment);

    SearchDepartmentRequest criteria = new SearchDepartmentRequest();
    SortRequest sortRequest = new SortRequest();
    sortRequest.setSortBy("department_code");
    sortRequest.setSortDirection("ASC");
    List<SortRequest> sorts = Arrays.asList(sortRequest);

    // When
    List<Department> results = departmentMapper.search(criteria, sorts, 10, 0);

    // Then
    org.junit.jupiter.api.Assertions.assertNotNull(results);
    org.junit.jupiter.api.Assertions.assertEquals(2, results.size());
    org.junit.jupiter.api.Assertions.assertEquals("HR", results.get(0).getDepartmentCode());
    org.junit.jupiter.api.Assertions.assertEquals("IT", results.get(1).getDepartmentCode());
  }

  @Test
  void count_SUCCESS1() {
    // Given
    departmentMapper.create(testDepartment);

    Department hrDepartment =
        Department.builder().departmentCode("HR").departmentName("Human Resources").build();
    departmentMapper.create(hrDepartment);

    SearchDepartmentRequest criteria = new SearchDepartmentRequest();
    criteria.setDepartmentName("Information");

    // When
    int count = departmentMapper.count(criteria);

    // Then
    org.junit.jupiter.api.Assertions.assertEquals(1, count);
  }
}
