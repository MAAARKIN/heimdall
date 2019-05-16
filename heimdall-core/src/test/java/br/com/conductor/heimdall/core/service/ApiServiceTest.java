package br.com.conductor.heimdall.core.service;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==========================LICENSE_END===================================
 */

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import br.com.conductor.heimdall.core.dto.ApiDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.ReferenceIdDTO;
import br.com.conductor.heimdall.core.dto.ResourceDTO;
import br.com.conductor.heimdall.core.dto.page.ApiPage;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Environment;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.enums.Status;
import br.com.conductor.heimdall.core.exception.BadRequestException;
import br.com.conductor.heimdall.core.exception.NotFoundException;
import br.com.conductor.heimdall.core.repository.ApiRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPRouteService;
import br.com.conductor.heimdall.core.util.Pageable;
import io.swagger.models.Swagger;

/**
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ApiServiceTest {


    @InjectMocks
    private ApiService apiService;
    
    @Mock
    private ResourceService resourceService;

    @Mock
    private ApiRepository apiRepository;

    @Mock
    private EnvironmentService environmentService;
    
    @Mock
    private SwaggerService swaggerService;

    @Mock
    private AMQPRouteService amqpRoute;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ApiDTO apiDTO;

    private Api api;

    @Before
    public void initAttributes() {
        api = new Api();
        api.setId(1L);
        api.setBasePath("/test");

        apiDTO = new ApiDTO();
        apiDTO.setName("test");
        apiDTO.setBasePath("/test");
        apiDTO.setDescription("test");
        apiDTO.setVersion("1.0.0");
        apiDTO.setStatus(Status.ACTIVE);
    }

    @Test
    public void saveTestWithSuccess() {
        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("http://localhost:8081");

        Environment e3 = new Environment();
        e3.setInboundURL("http://localhost:8082");

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(e3);

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        Api saved = apiService.save(apiDTO);

        assertEquals(saved.getId(), api.getId());
    }

    @Test
    public void saveTestWithEnvironmentNull() {

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("http://localhost:8081");

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(null);

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        Api saved = apiService.save(apiDTO);

        Mockito.verify(amqpRoute, Mockito.times(1)).dispatchRoutes();
        assertEquals(saved.getId(), api.getId());
    }

    @Test
    public void saveTestWithInboundEnvironmentsNull() {
        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();

        Environment e3 = new Environment();

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(e3);

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        Api saved = apiService.save(apiDTO);

        assertEquals(saved.getId(), api.getId());
    }

    @Test
    public void saveTestWithInboundEnvironmentsEmpty() {
        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("");

        Environment e3 = new Environment();
        e3.setInboundURL("");

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(e3);

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        Api saved = apiService.save(apiDTO);

        assertEquals(saved.getId(), api.getId());
    }

    @Test
    public void saveTestExpectedException() {

        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Apis can't have environments with the same inbound url");

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("http://localhost:8081");

        Environment e3 = new Environment();
        e3.setInboundURL("http://localhost:8081");

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(e3);

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        apiService.save(apiDTO);
    }
    
    @Test
    public void cantSaveWithApiExistent() {

        thrown.expect(BadRequestException.class);
        thrown.expectMessage("The basepath defined exist");

        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(api);

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));

        apiDTO.setEnvironments(environmentsDTO);
        apiService.save(apiDTO);
    }
    
    @Test
    public void cantSaveApiWithBasepathEmpty() {

        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Basepath not defined");

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));

        apiDTO.setEnvironments(environmentsDTO);
        
        apiDTO.setBasePath("");
        apiService.save(apiDTO);
    }
    
    @Test
    public void cantSaveApiWithBasepathNull() {

        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Basepath not defined");

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));

        apiDTO.setEnvironments(environmentsDTO);
        
        apiDTO.setBasePath(null);
        apiService.save(apiDTO);
    }

    @Test
    public void updateTestSuccess() {

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("http://localhost:8081");

        Environment e3 = new Environment();
        e3.setInboundURL("http://localhost:8082");

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(apiRepository.findOne(Mockito.anyLong())).thenReturn(api);
        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(api);
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(e3);

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        Api update = apiService.update(1L, apiDTO);

        assertEquals(update.getId(), api.getId());
    }

    @Test
    public void updateTestEnvironmentsNull() {

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("http://localhost:8081");

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(apiRepository.findOne(Mockito.anyLong())).thenReturn(api);
        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(api);
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(null);

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        Api update = apiService.update(1L, apiDTO);

        assertEquals(update.getId(), api.getId());
    }

    @Test
    public void updateTestInboundsEnvironmentNull() {

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();

        Environment e3 = new Environment();

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(apiRepository.findOne(Mockito.anyLong())).thenReturn(api);
        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(api);
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(e3);

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        Api update = apiService.update(1L, apiDTO);

        assertEquals(update.getId(), api.getId());
    }

    @Test
    public void updateTestInboundsEnvironmentEmpty() {

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("");

        Environment e3 = new Environment();
        e3.setInboundURL("");

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(apiRepository.findOne(Mockito.anyLong())).thenReturn(api);
        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(api);
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(e3);

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        Api update = apiService.update(1L, apiDTO);

        assertEquals(update.getId(), api.getId());
    }

    @Test
    public void updateTestExpectedException() {

        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Apis can't have environments with the same inbound url");

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("http://localhost:8080");

        Environment e3 = new Environment();
        e3.setInboundURL("http://localhost:8081");

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(apiRepository.findOne(Mockito.anyLong())).thenReturn(api);
        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(api);
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(e3);


        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        apiService.update(1L, apiDTO);
    }
    
    @Test
    public void listApisWithoutPagination() {
    	
    	Api api1 = new Api();
    	api1.setId(22L);
    	Api api2 = new Api();
    	api2.setId(36L);
    	
    	List<Api> asList = Arrays.asList(api1, api2);
    	Mockito.when(apiRepository.findAll(Matchers.<Example<Api>>any())).thenReturn(asList);
    	
    	List<Api> apis = apiService.list(apiDTO);
    	
    	assertEquals(asList.size(), apis.size());
    }
    
    @Test
    public void listApisWithPagination() {
    	
    	Api api1 = new Api();
    	api1.setId(22L);
    	Api api2 = new Api();
    	api2.setId(36L);
    	
    	List<Api> asList = Arrays.asList(api1, api2);
    	Page<Api> pageable = new PageImpl<>(asList);
    	Mockito.when(apiRepository.findAll(Matchers.<Example<Api>>any(), Mockito.any(Pageable.class))).thenReturn(pageable);
    	
    	PageableDTO page = new PageableDTO();
    	page.setLimit(10);
    	page.setOffset(5);
    	
    	ApiPage pages = apiService.list(apiDTO, page);
    	
    	assertEquals(asList.size(), pages.getContent().size());
    }
    
    @Test
    public void findApiWithId() {
    	
    	Api match = new Api();
    	api.setId(20L);
    	
    	Mockito.when(apiRepository.findOne(20L)).thenReturn(match);
    	Api find = apiService.find(20L);
    	
    	assertEquals(find.getId(), match.getId());
    }
    
    @Test
    public void catchResourceNotFoundWhenApiNotExist() {
    	thrown.expect(NotFoundException.class);
        thrown.expectMessage("Resource not found");
    	
    	Mockito.when(apiRepository.findOne(20L)).thenReturn(null);
    	apiService.find(20L);
    }
    
    @Test
    public void testSearchSwaggerByApi() {
    	
    	final String basePath = "/match";
    	
    	Swagger swaggerMatch = new Swagger();
    	swaggerMatch.basePath(basePath);
    	
    	Api match = new Api();
    	match.setId(22L);
    	
    	List<Resource> resources = new ArrayList<>();
    	Mockito.when(resourceService.list(Mockito.anyLong(), Mockito.any(ResourceDTO.class))).thenReturn(resources);
    	Mockito.when(apiRepository.findOne(22L)).thenReturn(match);
    	Mockito.when(swaggerService.exportApiToSwaggerJSON(match)).thenReturn(swaggerMatch);
    	
    	Swagger swaggerByApi = apiService.findSwaggerByApi(22L);
    	
    	assertEquals(basePath, swaggerByApi.getBasePath());
    }
    
    @Test
    public void searchPlansByApi() {
    	Api match = new Api();
    	match.setId(22L);
    	
    	Plan plan1 = new Plan();
    	plan1.setApi(match);
    	plan1.setId(5L);
    	Plan plan2 = new Plan();
    	plan1.setApi(match);
    	plan1.setId(6L);
    	
    	List<Plan> asList = Arrays.asList(plan1, plan2);
    	
    	match.setPlans(asList);
    	
    	Mockito.when(apiRepository.findOne(22L)).thenReturn(match);
    	
    	List<Plan> plansByApi = apiService.plansByApi(22L);
    	
    	assertEquals(asList, plansByApi);
    }
    
    @Test
    public void catchResourceNotFoundWhenUpdateAnApiNotExistent() {
    	thrown.expect(NotFoundException.class);
        thrown.expectMessage("Resource not found");
    	
    	Mockito.when(apiRepository.findOne(20L)).thenReturn(null);
    	apiService.update(20L, Mockito.any(ApiDTO.class));
    }
    
    @Test
    public void catchBadRequestWhenUpdateAnApiExist() {
    	thrown.expect(BadRequestException.class);
        thrown.expectMessage("The basepath defined exist");
        
        Api match = new Api();
    	match.setId(22L);
    	
        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(match);
        
    	Mockito.when(apiRepository.findOne(20L)).thenReturn(api);
    	apiService.update(20L, apiDTO);
    }
    
    @Test
    public void catchBadRequestWhenUpdateAnApiWithApiDTOUsingBasePathNull() {
    	thrown.expect(BadRequestException.class);
        thrown.expectMessage("Basepath not defined");
        
        Api match = new Api();
    	match.setId(22L);
    	
        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(match);
        
    	Mockito.when(apiRepository.findOne(22L)).thenReturn(api);
    	
    	apiDTO.setBasePath(null);
    	apiService.update(22L, apiDTO);
    }
    
    @Test
    public void catchBadRequestWhenUpdateAnApiWithApiDTOUsingBasePathEmpty() {
    	thrown.expect(BadRequestException.class);
        thrown.expectMessage("Basepath not defined");
        
        Api match = new Api();
    	match.setId(22L);
    	
        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(match);
        
    	Mockito.when(apiRepository.findOne(22L)).thenReturn(api);
    	
    	apiDTO.setBasePath("");
    	apiService.update(22L, apiDTO);
    }
    
    @Test
    public void catchBadRequestWhenNotExistApiWithBasePath() {
        Api match = new Api();
    	match.setId(22L);
    	
    	List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        apiDTO.setEnvironments(environmentsDTO);
    	
        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(null);
        
    	Mockito.when(apiRepository.findOne(22L)).thenReturn(api);

    	apiService.update(22L, apiDTO);
    	Mockito.verify(amqpRoute, Mockito.times(1)).dispatchRoutes();
    }
    
}
