package com.uit.sociusmvcapp.department.internal.service;

import com.uit.sociusmvcapp.department.DepartmentActionGuard;
import com.uit.sociusmvcapp.department.dto.DepartmentDto;
import com.uit.sociusmvcapp.department.dto.SearchDepartmentDto;
import com.uit.sociusmvcapp.department.dto.request.CreateDepartmentRequest;
import com.uit.sociusmvcapp.department.dto.request.SearchDepartmentRequest;
import com.uit.sociusmvcapp.department.enums.DepartmentActionType;
import com.uit.sociusmvcapp.department.internal.repository.DepartmentRepository;
import com.uit.sociusmvcapp.shared.constants.CommonConstant;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.exception.BusinessException;
import com.uit.sociusmvcapp.shared.request.PageRequest;
import com.uit.sociusmvcapp.shared.request.PaginationSearchRequest;
import com.uit.sociusmvcapp.shared.response.PageResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for DepartmentServiceImpl. Tests all methods with success and error scenarios. */
@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

  @Mock private DepartmentRepository departmentRepository;

  @Mock private List<DepartmentActionGuard> guards;

  @InjectMocks private DepartmentServiceImpl departmentService;

  private DepartmentDto departmentDto;
  private CreateDepartmentRequest createRequest;
  private SearchDepartmentRequest searchRequest;

  @BeforeEach
  void setUp() {
    departmentDto = new DepartmentDto();
    departmentDto.setDepartmentCode("IT");
    departmentDto.setDepartmentName("Information Technology");

    createRequest = new CreateDepartmentRequest();
    createRequest.setDepartmentCode("IT");
    createRequest.setDepartmentName("Information Technology");

    searchRequest = new SearchDepartmentRequest();
    searchRequest.setDepartmentCode("IT");
    searchRequest.setDepartmentName("Information");
  }

  @Test
  void findByDepartmentCode_SUCCESS1() {
    // Given
    String departmentCode = "IT";
    Mockito.when(departmentRepository.findByDepartmentCode(departmentCode))
        .thenReturn(departmentDto);

    // When
    DepartmentDto result = departmentService.findByDepartmentCode(departmentCode);

    // Then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(departmentCode, result.getDepartmentCode());
    Assertions.assertEquals("Information Technology", result.getDepartmentName());
    Mockito.verify(departmentRepository).findByDepartmentCode(departmentCode);
  }

  @Test
  void findByDepartmentCode_ERROR1() {
    // Given
    String departmentCode = "NONEXISTENT";
    Mockito.when(departmentRepository.findByDepartmentCode(departmentCode)).thenReturn(null);

    // When & Then
    BusinessException exception =
        Assertions.assertThrows(
            BusinessException.class, () -> departmentService.findByDepartmentCode(departmentCode));

    Assertions.assertEquals(MessageConstant.E_DEP_003, exception.getCode());
    Mockito.verify(departmentRepository).findByDepartmentCode(departmentCode);
  }

  @Test
  void create_SUCCESS1() {
    // Given
    Mockito.when(departmentRepository.findByDepartmentCode(createRequest.getDepartmentCode()))
        .thenReturn(null);
    Mockito.when(
            departmentRepository.findDeletedByDepartmentCode(createRequest.getDepartmentCode()))
        .thenReturn(null);

    // When
    departmentService.create(createRequest);

    // Then
    Mockito.verify(departmentRepository).findByDepartmentCode(createRequest.getDepartmentCode());
    Mockito.verify(departmentRepository)
        .findDeletedByDepartmentCode(createRequest.getDepartmentCode());
    Mockito.verify(departmentRepository).create(createRequest);
    Mockito.verify(departmentRepository, Mockito.never()).activate(Mockito.any());
  }

  @Test
  void create_SUCCESS2() {
    // Given - reactivate deleted department
    DepartmentDto deletedDepartment = new DepartmentDto();
    deletedDepartment.setDepartmentCode("IT");
    deletedDepartment.setDepartmentName("Information Technology");

    Mockito.when(departmentRepository.findByDepartmentCode(createRequest.getDepartmentCode()))
        .thenReturn(null);
    Mockito.when(
            departmentRepository.findDeletedByDepartmentCode(createRequest.getDepartmentCode()))
        .thenReturn(deletedDepartment);

    // When
    departmentService.create(createRequest);

    // Then
    Mockito.verify(departmentRepository).findByDepartmentCode(createRequest.getDepartmentCode());
    Mockito.verify(departmentRepository)
        .findDeletedByDepartmentCode(createRequest.getDepartmentCode());
    Mockito.verify(departmentRepository).activate(createRequest);
    Mockito.verify(departmentRepository, Mockito.never()).create(Mockito.any());
  }

  @Test
  void create_ERROR1() {
    // Given - department already exists
    Mockito.when(departmentRepository.findByDepartmentCode(createRequest.getDepartmentCode()))
        .thenReturn(departmentDto);

    // When & Then
    BusinessException exception =
        Assertions.assertThrows(
            BusinessException.class, () -> departmentService.create(createRequest));

    Assertions.assertEquals(MessageConstant.E_DEP_005, exception.getCode());
    Mockito.verify(departmentRepository).findByDepartmentCode(createRequest.getDepartmentCode());
    Mockito.verify(departmentRepository, Mockito.never())
        .findDeletedByDepartmentCode(Mockito.any());
    Mockito.verify(departmentRepository, Mockito.never()).create(Mockito.any());
  }

  @Test
  void update_SUCCESS1() {
    // Given
    String departmentCode = "IT";
    createRequest.setDepartmentCode("IT");
    Mockito.when(departmentRepository.findByDepartmentCode(departmentCode))
        .thenReturn(departmentDto);

    // When
    departmentService.update(createRequest, departmentCode);

    // Then
    Mockito.verify(departmentRepository).findByDepartmentCode(departmentCode);
    Mockito.verify(departmentRepository).update(createRequest);
  }

  @Test
  void update_ERROR1() {
    // Given - department code mismatch
    String departmentCode = "IT";
    createRequest.setDepartmentCode("HR");

    // When & Then
    BusinessException exception =
        Assertions.assertThrows(
            BusinessException.class, () -> departmentService.update(createRequest, departmentCode));

    Assertions.assertEquals(MessageConstant.E_DEP_002, exception.getCode());
    Mockito.verify(departmentRepository, Mockito.never()).findByDepartmentCode(Mockito.any());
  }

  @Test
  void update_ERROR2() {
    // Given - department not found
    String departmentCode = "NONEXISTENT";
    createRequest.setDepartmentCode(null);
    Mockito.when(departmentRepository.findByDepartmentCode(departmentCode)).thenReturn(null);

    // When & Then
    BusinessException exception =
        Assertions.assertThrows(
            BusinessException.class, () -> departmentService.update(createRequest, departmentCode));

    Assertions.assertEquals(MessageConstant.W_DEP_001, exception.getCode());
    Mockito.verify(departmentRepository).findByDepartmentCode(departmentCode);
    Mockito.verify(departmentRepository, Mockito.never()).update(Mockito.any());
  }

  @Test
  void deactivate_SUCCESS1() {
    // Given
    String departmentCode = "IT";
    List<DepartmentActionGuard> guardList = new ArrayList<>();
    DepartmentActionGuard mockGuard = Mockito.mock(DepartmentActionGuard.class);
    guardList.add(mockGuard);

    Mockito.when(departmentRepository.existsByDepartmentCode(departmentCode)).thenReturn(true);

    // Create a new instance with the mocked list
    DepartmentServiceImpl serviceWithGuards =
        new DepartmentServiceImpl(departmentRepository, guardList);

    // When
    serviceWithGuards.deactivate(departmentCode);

    // Then
    Mockito.verify(departmentRepository).existsByDepartmentCode(departmentCode);
    Mockito.verify(mockGuard).validate(DepartmentActionType.DEACTIVATE, departmentCode);
    Mockito.verify(departmentRepository).deactivate(departmentCode);
  }

  @Test
  void deactivate_ERROR1() {
    // Given - department not found
    String departmentCode = "NONEXISTENT";
    Mockito.when(departmentRepository.existsByDepartmentCode(departmentCode)).thenReturn(false);

    // When & Then
    BusinessException exception =
        Assertions.assertThrows(
            BusinessException.class, () -> departmentService.deactivate(departmentCode));

    Assertions.assertEquals(MessageConstant.W_DEP_001, exception.getCode());
    Mockito.verify(departmentRepository).existsByDepartmentCode(departmentCode);
    Mockito.verify(departmentRepository, Mockito.never()).deactivate(Mockito.any());
  }

  @Test
  void validateExists_SUCCESS1() {
    // Given
    String departmentCode = "IT";
    Mockito.when(departmentRepository.existsByDepartmentCode(departmentCode)).thenReturn(true);

    // When & Then - no exception should be thrown
    Assertions.assertDoesNotThrow(() -> departmentService.validateExists(departmentCode));

    Mockito.verify(departmentRepository).existsByDepartmentCode(departmentCode);
  }

  @Test
  void validateExists_ERROR1() {
    // Given
    String departmentCode = "NONEXISTENT";
    Mockito.when(departmentRepository.existsByDepartmentCode(departmentCode)).thenReturn(false);

    // When & Then
    BusinessException exception =
        Assertions.assertThrows(
            BusinessException.class, () -> departmentService.validateExists(departmentCode));

    Assertions.assertEquals(MessageConstant.W_DEP_001, exception.getCode());
    Mockito.verify(departmentRepository).existsByDepartmentCode(departmentCode);
  }

  @Test
  void search_SUCCESS1() {
    // Given
    SearchDepartmentDto searchDto1 =
        SearchDepartmentDto.builder()
            .departmentCode("IT")
            .departmentName("Information Technology")
            .build();
    SearchDepartmentDto searchDto2 =
        SearchDepartmentDto.builder()
            .departmentCode("HR")
            .departmentName("Human Resources")
            .build();
    List<SearchDepartmentDto> searchResults = Arrays.asList(searchDto1, searchDto2);

    PageRequest pageRequest = new PageRequest();
    pageRequest.setPageNumber(1);
    pageRequest.setPageSize(10);

    PaginationSearchRequest<SearchDepartmentRequest> request = new PaginationSearchRequest<>();
    request.setPageRequest(pageRequest);
    request.setCondition(searchRequest);
    request.setSortRequests(Collections.emptyList());

    Mockito.when(departmentRepository.count(searchRequest)).thenReturn(2);
    Mockito.when(departmentRepository.search(searchRequest, Collections.emptyList(), 10, 0))
        .thenReturn(searchResults);

    // When
    PageResponse<SearchDepartmentDto> result = departmentService.search(request);

    // Then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(2, result.getTotalItems());
    Assertions.assertEquals(2, result.getData().size());
    Mockito.verify(departmentRepository).count(searchRequest);
    Mockito.verify(departmentRepository).search(searchRequest, Collections.emptyList(), 10, 0);
  }

  @Test
  void search_SUCCESS2() {
    // Given - empty result
    PageRequest pageRequest = new PageRequest();
    pageRequest.setPageNumber(1);
    pageRequest.setPageSize(10);

    PaginationSearchRequest<SearchDepartmentRequest> request = new PaginationSearchRequest<>();
    request.setPageRequest(pageRequest);
    request.setCondition(searchRequest);
    request.setSortRequests(Collections.emptyList());

    Mockito.when(departmentRepository.count(searchRequest)).thenReturn(CommonConstant.INIT_INDEX);

    // When
    PageResponse<SearchDepartmentDto> result = departmentService.search(request);

    // Then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(0, result.getTotalItems());
    Assertions.assertTrue(result.getData().isEmpty());
    Mockito.verify(departmentRepository).count(searchRequest);
    Mockito.verify(departmentRepository, Mockito.never())
        .search(Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
  }
}
