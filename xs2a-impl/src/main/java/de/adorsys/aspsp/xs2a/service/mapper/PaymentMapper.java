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

package de.adorsys.aspsp.xs2a.service.mapper;

import de.adorsys.aspsp.xs2a.domain.AccountReference;
import de.adorsys.aspsp.xs2a.domain.Links;
import de.adorsys.aspsp.xs2a.domain.MessageCode;
import de.adorsys.aspsp.xs2a.domain.TransactionStatus;
import de.adorsys.aspsp.xs2a.domain.address.Address;
import de.adorsys.aspsp.xs2a.domain.ais.consent.AuthenticationObject;
import de.adorsys.aspsp.xs2a.domain.pis.PaymentInitialisationResponse;
import de.adorsys.aspsp.xs2a.domain.pis.PeriodicPayment;
import de.adorsys.aspsp.xs2a.domain.pis.Remittance;
import de.adorsys.aspsp.xs2a.domain.pis.SinglePayments;
import de.adorsys.aspsp.xs2a.spi.domain.account.SpiAccountReference;
import de.adorsys.aspsp.xs2a.spi.domain.common.SpiTransactionStatus;
import de.adorsys.aspsp.xs2a.spi.domain.payment.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PaymentMapper {

    private final ConsentMapper consentMapper;
    private final AccountMapper accountMapper;

    public TransactionStatus mapGetPaymentStatusById(SpiTransactionStatus spiTransactionStatus) {
        return Optional.ofNullable(spiTransactionStatus)
                       .map(ts -> TransactionStatus.valueOf(ts.name()))
                       .orElse(null);
    }

    public SpiSinglePayments mapToSpiSinglePayments(SinglePayments paymentInitiationRequest) {
        return Optional.ofNullable(paymentInitiationRequest)
                       .map(paymentRe -> {
                           SpiSinglePayments spiSinglePayments = new SpiSinglePayments();
                           spiSinglePayments.setEndToEndIdentification(paymentRe.getEndToEndIdentification());
                           spiSinglePayments.setDebtorAccount(mapAccountReference(paymentRe.getDebtorAccount()));
                           spiSinglePayments.setUltimateDebtor(paymentRe.getUltimateDebtor());
                           spiSinglePayments.setInstructedAmount(accountMapper.mapToSpiAmount(paymentRe.getInstructedAmount()));
                           spiSinglePayments.setCreditorAccount(mapAccountReference(paymentRe.getCreditorAccount()));
                           spiSinglePayments.setCreditorAgent(paymentRe.getCreditorAgent().getCode());
                           spiSinglePayments.setCreditorName(paymentRe.getCreditorName());
                           spiSinglePayments.setCreditorAddress(mapToSpiAddress(paymentRe.getCreditorAddress()));
                           spiSinglePayments.setUltimateCreditor(paymentRe.getUltimateCreditor());
                           spiSinglePayments.setPurposeCode(paymentRe.getPurposeCode().getCode());
                           spiSinglePayments.setRemittanceInformationUnstructured(paymentRe.getRemittanceInformationUnstructured());
                           spiSinglePayments.setRemittanceInformationStructured(mapToSpiRemittance(paymentRe.getRemittanceInformationStructured()));
                           spiSinglePayments.setRequestedExecutionDate(paymentRe.getRequestedExecutionDate());
                           spiSinglePayments.setRequestedExecutionTime(paymentRe.getRequestedExecutionTime());

                           return spiSinglePayments;
                       })
                       .orElse(null);
    }

    private SpiAccountReference mapAccountReference(AccountReference accountReference) {
        return Optional.ofNullable(accountReference)
                       .map(ar -> {
                           SpiAccountReference spiAccountReference = new SpiAccountReference(
                                   ar.getAccountId(),
                                   ar.getIban(),
                                   ar.getBban(),
                                   ar.getPan(),
                                   ar.getMaskedPan(),
                                   ar.getMsisdn(),
                                   ar.getCurrency());
                           return spiAccountReference;
                       })
                       .orElse(null);
    }

    public SpiPeriodicPayment mapToSpiPeriodicPayment(PeriodicPayment periodicPayment) {
        return Optional.ofNullable(periodicPayment)
                       .map(pp -> {
                           SpiPeriodicPayment spiPeriodicPayment = new SpiPeriodicPayment();
                           spiPeriodicPayment.setEndToEndIdentification(pp.getEndToEndIdentification());
                           spiPeriodicPayment.setDebtorAccount(consentMapper.mapToSpiAccountReference(pp.getDebtorAccount()));
                           spiPeriodicPayment.setUltimateDebtor(pp.getUltimateDebtor());
                           spiPeriodicPayment.setInstructedAmount(accountMapper.mapToSpiAmount(pp.getInstructedAmount()));
                           spiPeriodicPayment.setCreditorAccount(consentMapper.mapToSpiAccountReference(pp.getCreditorAccount()));
                           spiPeriodicPayment.setCreditorAgent(pp.getCreditorAgent() != null ? pp.getCreditorAgent().getCode() : null);
                           spiPeriodicPayment.setCreditorName(pp.getCreditorName());
                           spiPeriodicPayment.setCreditorAddress(mapToSpiAddress(pp.getCreditorAddress()));
                           spiPeriodicPayment.setUltimateCreditor(pp.getUltimateCreditor());
                           spiPeriodicPayment.setPurposeCode(pp.getPurposeCode() != null ? pp.getPurposeCode().getCode() : null);
                           spiPeriodicPayment.setRemittanceInformationUnstructured(pp.getRemittanceInformationUnstructured());
                           spiPeriodicPayment.setRemittanceInformationStructured(mapToSpiRemittance(pp.getRemittanceInformationStructured()));
                           spiPeriodicPayment.setRequestedExecutionDate(pp.getRequestedExecutionDate());
                           spiPeriodicPayment.setRequestedExecutionTime(pp.getRequestedExecutionTime());

                           spiPeriodicPayment.setStartDate(pp.getStartDate());
                           spiPeriodicPayment.setExecutionRule(pp.getExecutionRule());
                           spiPeriodicPayment.setEndDate(pp.getEndDate());
                           spiPeriodicPayment.setFrequency(pp.getFrequency() != null ? pp.getFrequency().name() : null);
                           spiPeriodicPayment.setDayOfExecution(pp.getDayOfExecution());

                           return spiPeriodicPayment;

                       }).orElse(null);
    }

    public PaymentInitialisationResponse mapFromSpiPaymentInitializationResponse(SpiPaymentInitialisationResponse response) {

        return Optional.ofNullable(response)
                       .map(pir -> {
                           PaymentInitialisationResponse initialisationResponse = new PaymentInitialisationResponse();
                           initialisationResponse.setTransactionStatus(consentMapper.mapFromSpiTransactionStatus(pir.getTransactionStatus()));
                           initialisationResponse.setPaymentId(pir.getPaymentId());
                           initialisationResponse.setTransactionFees(accountMapper.mapFromSpiAmount(pir.getSpiTransactionFees()));
                           initialisationResponse.setTransactionFeeIndicator(pir.isSpiTransactionFeeIndicator());
                           initialisationResponse.setPsuMessage(pir.getPsuMessage());
                           initialisationResponse.setTppRedirectPreferred(pir.isTppRedirectPreferred());
                           initialisationResponse.setScaMethods(mapFromSpiAuthenticationObjects(pir.getScaMethods()));
                           initialisationResponse.setTppMessages(mapFromSpiMessageCodes(pir.getTppMessages()));
                           initialisationResponse.setLinks(new Links());
                           return initialisationResponse;
                       }).orElse(null);
    }

    private AuthenticationObject[] mapFromSpiAuthenticationObjects(String[] authObjects) { //NOPMD TODO review and check PMD assertion
        return new AuthenticationObject[]{};//TODO Fill in th Linx
    }

    private MessageCode[] mapFromSpiMessageCodes(String[] messageCodes) { //NOPMD TODO review and check PMD assertion
        return new MessageCode[]{};//TODO Fill in th Linx
    }

    private SpiAddress mapToSpiAddress(Address address) {
        return Optional.ofNullable(address)
                       .map(a -> new SpiAddress(null, a.getStreet(), a.getBuildingNumber(), a.getCity(), a.getPostalCode(), a.getCountry().toString()))
                       .orElse(null);
    }

    private SpiRemittance mapToSpiRemittance(Remittance remittance) {
        return Optional.ofNullable(remittance)
                       .map(r -> {
                           SpiRemittance spiRemittance = new SpiRemittance();
                           spiRemittance.setReference(r.getReference());
                           spiRemittance.setReferenceType(r.getReferenceType());
                           spiRemittance.setReferenceIssuer(r.getReferenceIssuer());
                           return spiRemittance;
                       }).orElse(null);
    }

    public List<SpiSinglePayments> mapToSpiSinglePaymentList(List<SinglePayments> payments) {
        return payments.stream().map(this::mapToSpiSinglePayments)
                       .collect(Collectors.toList());
    }
}
