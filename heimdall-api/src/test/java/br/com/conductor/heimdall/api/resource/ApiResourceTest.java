/*-
 * =========================LICENSE_START==================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
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
package br.com.conductor.heimdall.api.resource;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import br.com.conductor.heimdall.api.ApiApplication;
import br.com.conductor.heimdall.api.service.TokenAuthenticationService;
import br.com.conductor.heimdall.core.dto.ApiDTO;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.service.ApiService;
import br.com.conductor.heimdall.core.util.ConstantsPath;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ApiApplication.class)
@ContextConfiguration
@WebAppConfiguration
@AutoConfigureMockMvc
public class ApiResourceTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TokenAuthenticationService tokenAuthenticationService;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private FilterChainProxy filterChain;

	@MockBean
	private ApiService apiService;

	@Before
	public void setupTest() {

		Authentication authentication = new UsernamePasswordAuthenticationToken("tester", "password",
				Arrays.asList(new SimpleGrantedAuthority("CREATE_API")));

		Mockito.when(tokenAuthenticationService.getAuthentication(Mockito.any(), Mockito.any()))
				.thenReturn(authentication);
		mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity())
				.addFilter(filterChain).build();
	}

	@Test
	@WithMockUser(username = "tester", authorities = { "CREATE_API" })
	public void testSavingApi() throws Exception {
		Api api = new Api();
		api.setId(10L);

		Mockito.when(apiService.save(Mockito.any(ApiDTO.class))).thenReturn(api);
		
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("\"basePath\": \"/basePath\",");
		builder.append("\"description\": \"description\",");
		builder.append("\"destinationProduction\": \"\",");
		builder.append("\"destinationSandbox\": \"\",");
		builder.append("\"name\": \"myname\",");
		builder.append("\"status\": \"ACTIVE\",");
		builder.append("\"version\": \"version\"");
		builder.append("}");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ConstantsPath.PATH_APIS, 10L)
				.content(builder.toString())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isCreated());
	}
}
