package com.uit.sociusmvcapp.department;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uit.sociusmvcapp.department.dto.DepartmentDto;
import com.uit.sociusmvcapp.department.dto.SearchDepartmentDto;
import com.uit.sociusmvcapp.department.dto.request.CreateDepartmentRequest;
import com.uit.sociusmvcapp.department.dto.request.SearchDepartmentRequest;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.request.PageRequest;
import com.uit.sociusmvcapp.shared.request.PaginationSearchRequest;
import com.uit.sociusmvcapp.shared.response.PageResponse;
import com.uit.sociusmvcapp.shared.service.I18nService;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/** Unit tests for DepartmentController. Tests all endpoints with success scenarios. */
@WebMvcTest(DepartmentController.class)
@ActiveProfiles("test")
class DepartmentControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private DepartmentService departmentService;

  @MockitoBean private I18nService i18nService;

  private DepartmentDto departmentDto;
  private CreateDepartmentRequest createRequest;

  @BeforeEach
  void setUp() {
    departmentDto = new DepartmentDto();
    departmentDto.setDepartmentCode("IT");
    departmentDto.setDepartmentName("Information Technology");

    createRequest = new CreateDepartmentRequest();
    createRequest.setDepartmentCode("IT");
    createRequest.setDepartmentName("Information Technology");

    // Mock i18nService to return message codes
    Mockito.when(i18nService.getMessage(Mockito.anyString()))
        .thenAnswer(invocation -> invocation.getArgument(0));
  }

  @Test
  @WithMockUser
  void findByDepartmentCode_SUCCESS1() throws Exception {
    // Given
    String departmentCode = "IT";
    Mockito.when(departmentService.findByDepartmentCode(departmentCode)).thenReturn(departmentDto);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/departments/{departmentCode}", departmentCode)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
        .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(MessageConstant.S_DEP_003))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.departmentCode").value("IT"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.data.departmentName")
                .value("Information Technology"));

    Mockito.verify(departmentService).findByDepartmentCode(departmentCode);
  }

  @Test
  @WithMockUser
  void create_SUCCESS1() throws Exception {
    // Given
    Mockito.doNothing().when(departmentService).create(Mockito.any(CreateDepartmentRequest.class));

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
        .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(MessageConstant.S_DEP_001));

    Mockito.verify(departmentService).create(Mockito.any(CreateDepartmentRequest.class));
  }

  @Test
  @WithMockUser
  void update_SUCCESS1() throws Exception {
    // Given
    String departmentCode = "IT";
    Mockito.doNothing()
        .when(departmentService)
        .update(Mockito.any(CreateDepartmentRequest.class), Mockito.eq(departmentCode));

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/departments/{departmentCode}", departmentCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
        .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(MessageConstant.S_DEP_002));

    Mockito.verify(departmentService)
        .update(Mockito.any(CreateDepartmentRequest.class), Mockito.eq(departmentCode));
  }

  @Test
  @WithMockUser
  void deactivate_SUCCESS1() throws Exception {
    // Given
    String departmentCode = "IT";
    Mockito.doNothing().when(departmentService).deactivate(departmentCode);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/departments/{departmentCode}", departmentCode)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
        .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(MessageConstant.S_DEP_004));

    Mockito.verify(departmentService).deactivate(departmentCode);
  }

  @Test
  @WithMockUser
  void search_SUCCESS1() throws Exception {
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

    PageResponse<SearchDepartmentDto> pageResponse =
        PageResponse.of(Arrays.asList(searchDto1, searchDto2), 2, 0, 10);

    SearchDepartmentRequest searchRequest = new SearchDepartmentRequest();
    searchRequest.setDepartmentCode("IT");

    PageRequest pageRequest = new PageRequest();
    pageRequest.setPageNumber(1);
    pageRequest.setPageSize(10);

    PaginationSearchRequest<SearchDepartmentRequest> request = new PaginationSearchRequest<>();
    request.setPageRequest(pageRequest);
    request.setCondition(searchRequest);
    request.setSortRequests(Collections.emptyList());

    Mockito.when(departmentService.search(Mockito.any(PaginationSearchRequest.class)))
        .thenReturn(pageResponse);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/departments/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
        .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(MessageConstant.S_DEP_005))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalItems").value(2));

    Mockito.verify(departmentService).search(Mockito.any(PaginationSearchRequest.class));
  }
}
