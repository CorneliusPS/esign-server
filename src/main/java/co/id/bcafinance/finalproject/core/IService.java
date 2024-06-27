package co.id.bcafinance.finalproject.core;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface IService<T> {
    ResponseEntity<Object> save(T t, String authorizationHeader, HttpServletRequest request); //001 - 010
    ResponseEntity<Object> findById(Long id, HttpServletRequest request); //011 - 020
    ResponseEntity<Object> delete(Long id, String authorizationHeader, HttpServletRequest request); //021 - 030
    ResponseEntity<Object> update(Long id, T t, String authorizationHeader, HttpServletRequest request); //031 - 040
    ResponseEntity<Object> find(Pageable pageable, String filterBy, String value, String authorizationHeader, HttpServletRequest request);
    ResponseEntity<Object> findWithoutPage(HttpServletRequest request);
}

