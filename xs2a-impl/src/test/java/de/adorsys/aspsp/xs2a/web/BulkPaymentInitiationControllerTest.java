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

package de.adorsys.aspsp.xs2a.web;

import com.google.gson.Gson;
import de.adorsys.aspsp.xs2a.domain.ResponseObject;
import de.adorsys.aspsp.xs2a.domain.pis.PaymentInitialisationResponse;
import de.adorsys.aspsp.xs2a.domain.pis.PaymentProduct;
import de.adorsys.aspsp.xs2a.domain.pis.SinglePayments;
import de.adorsys.aspsp.xs2a.service.PaymentService;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BulkPaymentInitiationControllerTest {
    private final String BULK_PAYMENT_DATA = "/json/BulkPaymentTestData.json";
    private final String BULK_PAYMENT_RESP_DATA = "/json/BulkPaymentResponseTestData.json";
    private final Charset UTF_8 = Charset.forName("utf-8");

    @Autowired
    private BulkPaymentInitiationController bulkPaymentInitiationController;

    @MockBean(name = "paymentService")
    private PaymentService paymentService;

    @Before
    public void setUp() throws IOException {
        when(paymentService.createBulkPayments(any(), any(), anyBoolean())).thenReturn(readResponseObject());
    }

    @Test
    public void createBulkPaymentInitiation() throws IOException {
        //Given
        PaymentProduct paymentProduct = PaymentProduct.SCT;
        boolean tppRedirectPreferred = false;
        List<SinglePayments> payments = readBulkPayments();
        ResponseEntity<List<PaymentInitialisationResponse>> expectedResult = new ResponseEntity<>(readPaymentInitialisationResponse(), HttpStatus.CREATED);

        //When:
        ResponseEntity<List<PaymentInitialisationResponse>> actualResult = bulkPaymentInitiationController
                                                                       .createBulkPaymentInitiation(paymentProduct.getCode(), tppRedirectPreferred, payments);

        //Then:
        assertThat(actualResult.getStatusCode()).isEqualTo(expectedResult.getStatusCode());
        assertThat(actualResult.getBody()).isEqualTo(expectedResult.getBody());
    }

    private ResponseObject<List<PaymentInitialisationResponse>> readResponseObject() throws IOException {
        return ResponseObject.builder()
               .body(readPaymentInitialisationResponse()).build();
    }

    private List<PaymentInitialisationResponse> readPaymentInitialisationResponse() throws IOException {
        PaymentInitialisationResponse response = new Gson().fromJson(IOUtils.resourceToString(BULK_PAYMENT_RESP_DATA, UTF_8), PaymentInitialisationResponse.class);
        List<PaymentInitialisationResponse> responseList = new ArrayList<>();
        responseList.add(response);

        return responseList;
    }

    private List<SinglePayments> readBulkPayments() throws IOException {
        SinglePayments[] payments = new Gson().fromJson(IOUtils.resourceToString(BULK_PAYMENT_DATA, UTF_8), SinglePayments[].class);
        return Arrays.asList(payments);
    }
}
