package de.adorsys.aspsp.xs2a.web;

import de.adorsys.aspsp.xs2a.domain.ResponseObject;
import de.adorsys.aspsp.xs2a.domain.pis.PaymentInitialisationResponse;
import de.adorsys.aspsp.xs2a.domain.pis.PeriodicPayment;
import de.adorsys.aspsp.xs2a.service.PaymentService;
import de.adorsys.aspsp.xs2a.service.mapper.ResponseMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/periodic-payments")
@Api(value = "api/v1/periodic-payments", tags = "PISP, Payments", description = "Orders for periodic payments")
public class PeriodicPaymentsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);
    private PaymentService paymentService;
    private ResponseMapper responseMapper;

    @Autowired
    public PeriodicPaymentsController(PaymentService paymentService, ResponseMapper responseMapper) {
        this.paymentService = paymentService;
        this.responseMapper = responseMapper;
    }

    @ApiOperation(value = "The TPP can submit a recurring payment initiation where the starting date, frequency and conditionally an end date is provided. Once authorised by the PSU, the payment then will be executed by the ASPSP, if possible, following this “standing order” as submitted by the TPP.")
    @ApiResponses(value = {
    @ApiResponse(code = 200, message = "OK"),
    @ApiResponse(code = 400, message = "Bad request")})
    @RequestMapping(value = "/{payment-product}", method = RequestMethod.POST)
    @ApiImplicitParams({
    @ApiImplicitParam(name = "psu-ip-address", value = "192.168.0.26", required = true, paramType = "header"),
    @ApiImplicitParam(name = "tpp-transaction-id", value = "16d40f49-a110-4344-a949-f99828ae13c9", required = true, dataType = "UUID", paramType = "header"),
    @ApiImplicitParam(name = "tpp-request-id", value = "21d40f65-a150-8343-b539-b9a822ae98c0", required = true, dataType = "UUID", paramType = "header")})
    public ResponseEntity<PaymentInitialisationResponse> initiationForStandingOrdersForRecurringOrPeriodicPayments(
    @ApiParam(name = "payment-product", value = "Named payment product", example = "sepa-credit-transfers")
    @PathVariable(name = "payment-product", required = true) String paymentProduct,
    @ApiParam(name = "tppRedirectPreferred", value = "If it equals “true”, the TPP prefers a redirect over an embedded SCA approach.")
    @RequestParam(name = "tppRedirectPreferred", required = false) boolean tppRedirectPreferred,
    @ApiParam(name = "Periodic Payment", value = "All data relevant for the corresponding payment product and necessary for execution of the standing order.")
    @RequestBody PeriodicPayment periodicPayment) {
        ResponseObject responseObject = paymentService.initiatePeriodicPayment(paymentProduct, tppRedirectPreferred, periodicPayment);

        return responseMapper.okOrByTransactionStatus(responseObject);
    }

}