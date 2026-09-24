package com.mycompany.webstore

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.junit.jupiter.api.Test

@QuarkusTest
class WebstoreApplicationTest {

    @Test
    fun `health endpoint should return 200`() {
        given()
            .`when`().get("/q/health")
            .then().statusCode(200)
    }

    @Test
    fun `openapi endpoint should return 200`() {
        given()
            .`when`().get("/q/openapi")
            .then().statusCode(200)
    }

    @Test
    fun `products endpoint should return 200`() {
        given()
            .`when`().get("/api/products")
            .then().statusCode(200)
    }

    @Test
    fun `categories endpoint should return 200`() {
        given()
            .`when`().get("/api/categories")
            .then().statusCode(200)
    }
}
