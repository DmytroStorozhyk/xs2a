/*
 * Copyright 2018-2018 adorsys GmbH & Co KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.adorsys.aspsp.xs2a.service.validator;


import de.adorsys.aspsp.xs2a.web.ConsentInformationController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RequestValidatorServiceTest {

    @Autowired
    private RequestValidatorService requestValidatorService;
    @Autowired
    private ConsentInformationController consentInformationController;

    @Test
    public void getRequestHeaderViolationMap() throws Exception {
        //Given:
        HttpServletRequest request = getCorrectRequest();
        Object handler = getHandler();

        //When:
        Map<String, String> actualViolations = requestValidatorService.getRequestHeaderViolationMap(request, handler);

        //Then:
        assertThat(actualViolations.isEmpty()).isTrue();
    }

    @Test
    public void shouldFail_getRequestHeaderViolationMap_wrongRequest() throws Exception {
        //Given:
        HttpServletRequest request = getWrongRequestNoTppRequestId();
        Object handler = getHandler();

        //When:
        Map<String, String> actualViolations = requestValidatorService.getRequestHeaderViolationMap(request, handler);

        //Then:
        assertThat(actualViolations.size()).isEqualTo(1);
    }

    @Test
    public void shouldFail_getRequestHeaderViolationMap_wrongRequestHeaderFormat() throws Exception {
        //Given:
        HttpServletRequest request = getWrongRequestWrongTppRequestIdFormat();
        Object handler = getHandler();

        //When:
        Map<String, String> actualViolations = requestValidatorService.getRequestHeaderViolationMap(request, handler);

        //Then:
        assertThat(actualViolations.size()).isEqualTo(1);
        assertThat(actualViolations.get("Wrong header arguments: ")).contains("Can not deserialize value");
    }

    private HttpServletRequest getWrongRequestNoTppRequestId() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Content-Type", "application/json");
        request.addHeader("tpp-transaction-id", "16d40f49-a110-4344-a949-f99828ae13c9");
        request.addHeader("consent-id", "21d40f65-a150-8343-b539-b9a822ae98c0");

        return request;
    }

    private HttpServletRequest getWrongRequestWrongTppRequestIdFormat() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Content-Type", "application/json");
        request.addHeader("tpp-transaction-id", "16d40f49-a110-4344-a949-f99828ae13c9");
        request.addHeader("tpp-request-id", "wrong_format");
        request.addHeader("consent-id", "21d40f65-a150-8343-b539-b9a822ae98c0");

        return request;
    }

    private HttpServletRequest getCorrectRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Content-Type", "application/json");
        request.addHeader("tpp-transaction-id", "16d40f49-a110-4344-a949-f99828ae13c9");
        request.addHeader("tpp-request-id", "21d40f65-a150-8343-b539-b9a822ae98c0");
        request.addHeader("consent-id", "21d40f65-a150-8343-b539-b9a822ae98c0");

        return request;
    }

    private Object getHandler() throws NoSuchMethodException {
        return new HandlerMethod(consentInformationController, "getAccountConsentsInformationById", String.class);
    }
}
