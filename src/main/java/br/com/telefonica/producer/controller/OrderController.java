package br.com.telefonica.producer.controller;

import java.util.List;
import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.telefonica.producer.model.Order;
import br.com.telefonica.producer.service.KafkaProdService;
import br.com.telefonica.producer.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    KafkaProdService producer;

    @Autowired
    OrderService orderService;
    
    @GetMapping
    public ResponseEntity<Iterable<Order>> list(){
        return ResponseEntity.ok(orderService.findAll());
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody @Valid OrderTO request){
        Order order = request.createFromTO();
        orderService.save(order);
        producer.send(order);
        return ResponseEntity.created(null).body(order);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Order>> search(@RequestParam(required = false, name = "max_total") Double maxTotal,
                                              @RequestParam(required = false, name = "min_total") Double minTotal,
                                              @RequestParam(required = false, name = "q") String query){
        List<Order> list = orderService.search(query, minTotal, maxTotal);
        if(list.size()>0){
            return ResponseEntity.ok(list);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> findById(@PathVariable Long id){
        Order order = null;
        try{
            order = orderService.findById(id).get();
        }catch(NoSuchElementException nsee){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Long id, @RequestBody @Valid OrderTO request){
        Order order = request.createFromTO();
        if(!orderService.findById(id).isPresent())
            return ResponseEntity.notFound().build();
        order.setId(id);
        order = orderService.save(order);
        producer.send(order);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Order> delete(@PathVariable Long id){
        try{
            orderService.delete(orderService.findById(id).get());
        }catch(NoSuchElementException nsee){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

}