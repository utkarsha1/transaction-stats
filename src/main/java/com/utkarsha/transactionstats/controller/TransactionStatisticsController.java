package com.utkarsha.transactionstats.controller;

import com.utkarsha.transactionstats.service.StatisticsService;
import com.utkarsha.transactionstats.service.TransactionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TransactionStatisticsController {

    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public ResponseEntity<?> statistics() {
        return new ResponseEntity<>(StatisticsService.getStatistics(), HttpStatus.OK);
    }

    @RequestMapping(value = "/transactions", method = RequestMethod.POST)
    public ResponseEntity<?> add(@RequestBody Map<String, Object> payload) {
        String amount = String.valueOf(payload.get("amount"));
        String timestamp = String.valueOf(payload.get("timestamp"));

        if(StringUtils.isBlank((CharSequence) payload.get("amount")) ||
            StringUtils.isBlank((CharSequence) payload.get("timestamp"))) {
            return new ResponseEntity<>(HttpStatus.valueOf(422));
        }
        return new ResponseEntity<>(HttpStatus.valueOf(TransactionService.add(amount, timestamp)));
    }

    @RequestMapping(value = "/transactions" , method = RequestMethod.DELETE)
    public ResponseEntity<?> delete() {
        return new ResponseEntity<>(HttpStatus.valueOf(TransactionService.deleteAll()));
    }

    @RequestMapping(value = "/health", method = RequestMethod.GET)
    public ResponseEntity<?> up() {
        return new ResponseEntity<>("Service is up!", HttpStatus.OK);
    }
}