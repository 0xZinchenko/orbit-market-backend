package com.zim4ik.spacecatmarket.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.zim4ik.spacecatmarket.product.dto.ProductDTO;
import com.zim4ik.spacecatmarket.product.service.ProductService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.github.tomakehurst.wiremock.client.WireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class JwtResourceServerIT {

    private static final String KEY_ID = "test-key";

    @Container
    static final WireMockContainer WIRE_MOCK = new WireMockContainer("wiremock/wiremock:3.9.1");

    private static RSAKey trustedSigningKey;
    private static RSAKey untrustedSigningKey;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                () -> WIRE_MOCK.getBaseUrl() + "/.well-known/jwks.json");
    }

    @BeforeAll
    static void generateKeysAndStubJwks() throws Exception {
        trustedSigningKey = generateRsaKey(KEY_ID);
        untrustedSigningKey = generateRsaKey("untrusted-key");

        configureFor(WIRE_MOCK.getHost(), WIRE_MOCK.getMappedPort(8080));
        stubFor(WireMock.get(urlEqualTo("/.well-known/jwks.json"))
                .willReturn(okJson("{\"keys\":[" + trustedSigningKey.toPublicJWK().toJSONString() + "]}")));
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void getProducts_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getProducts_withTokenSignedByUntrustedKey_returns401() throws Exception {
        String token = signToken(untrustedSigningKey, KEY_ID);

        mockMvc.perform(get("/api/v1/products").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getProducts_withValidToken_returns200() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(
                new ProductDTO(1L, "Galaxy Cat", java.math.BigDecimal.valueOf(100), null)));
        String token = signToken(trustedSigningKey, KEY_ID);

        mockMvc.perform(get("/api/v1/products").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private static RSAKey generateRsaKey(String keyId) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();

        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID(keyId)
                .build();
    }

    private static String signToken(RSAKey signingKey, String keyId) throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("nebula-cat")
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plusSeconds(300)))
                .claim("scope", "products.read")
                .jwtID(UUID.randomUUID().toString())
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(keyId).build(),
                claims);
        signedJWT.sign(new RSASSASigner(signingKey));

        return signedJWT.serialize();
    }
}
