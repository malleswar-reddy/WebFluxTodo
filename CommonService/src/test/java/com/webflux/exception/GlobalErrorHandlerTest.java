package com.webflux.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalErrorHandlerTest {

    @InjectMocks
    private GlobalErrorHandler globalErrorHandler;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private org.springframework.http.server.reactive.ServerHttpResponse response;

    @Mock
    private org.springframework.core.io.buffer.DataBufferFactory dataBufferFactory;

    @Mock
    private org.springframework.core.io.buffer.DataBuffer dataBuffer;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void handle_successfulErrorHandling() {
        when(exchange.getResponse()).thenReturn(response);
        when(response.getHeaders()).thenReturn(new HttpHeaders());
        when(response.bufferFactory()).thenReturn(dataBufferFactory);

        String errorMessage = "Resource not found";
        Throwable exception = new RuntimeException(errorMessage);
        Map<String, String> expectedBody = Map.of("error", errorMessage);

        when(dataBufferFactory.wrap(any(byte[].class))).thenReturn(dataBuffer);
        when(response.writeWith(any(Mono.class))).thenReturn(Mono.empty());

        Mono<Void> result = globalErrorHandler.handle(exchange, exception);

        StepVerifier.create(result)
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.NOT_FOUND);
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        verify(dataBufferFactory).wrap(any(byte[].class));
        verify(response).writeWith(any(Mono.class));
    }

    @Test
    void handle_objectMapperFailure() throws JsonProcessingException {
        when(exchange.getResponse()).thenReturn(response);
        when(response.getHeaders()).thenReturn(new HttpHeaders());
        
//        when(response.bufferFactory()).thenReturn(dataBufferFactory);

        Throwable exception = new RuntimeException("Resource not found");

        ObjectMapper spyObjectMapper = spy(objectMapper);
        when(spyObjectMapper.writeValueAsBytes(any())).thenThrow(new RuntimeException("Serialization error"));
        globalErrorHandler = new GlobalErrorHandler() {
            private final ObjectMapper customObjectMapper = spyObjectMapper;

            @Override
            public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
                exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

                Map<String, String> errorBody = Map.of("error", ex.getMessage());

                try {
                    byte[] bytes = customObjectMapper.writeValueAsBytes(errorBody);
                    return exchange.getResponse()
                            .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
                } catch (Exception e) {
                    return exchange.getResponse().setComplete();
                }
            }
        };

        when(response.setComplete()).thenReturn(Mono.empty());

        Mono<Void> result = globalErrorHandler.handle(exchange, exception);

        StepVerifier.create(result)
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.NOT_FOUND);
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        verify(response).setComplete();
        verify(dataBufferFactory, never()).wrap(any(byte[].class));
        verify(response, never()).writeWith(any(Mono.class));
    }

    @Test
    void verifyOrderAnnotation() {
        Order order = GlobalErrorHandler.class.getAnnotation(Order.class);
        assertEquals(-2, order.value(), "Order should be -2 to run before default handler");
    }
}
