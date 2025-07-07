package com.manager.truck.truck.usecase.charge;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.dto.request.ChargeRequest;
import com.manager.truck.truck.domain.dto.response.ChargeResponse;
import com.manager.truck.truck.service.ChargeService;
import com.manager.truck.truck.service.ClientService;
import com.manager.truck.truck.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaveChargeUseCase {
    @Autowired
    private ChargeService chargeService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ContractService contractService;

    public ChargeResponse execute(ChargeRequest chargeRequest,Long id,Long contractId){
        Charge charge = chargeBuild(chargeRequest, id,contractId);
        chargeService.save(charge);
        return chargeResponseBuild(charge);
    }

    private Charge chargeBuild(ChargeRequest chargeRequest, Long id,Long contractId){
        Charge charge = new Charge();
        Client client = clientService.GetById(id);
        Contract contract = contractService.findById(contractId);

        charge.setDescription( chargeRequest.getDescription());
        charge.setType(chargeRequest.getType());
        charge.setClient(client);
        charge.setContract(contract);
        return charge;
    }
    private ChargeResponse chargeResponseBuild(Charge charge){
        return new ChargeResponse(charge);
    }



}
