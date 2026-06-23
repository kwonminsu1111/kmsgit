package com.ssafy.enjoytrip.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        String securityJwtName = "Authorization";

        // JWT Bearer 인증 방식 스펙 정의
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)     // HTTP 방식
                .scheme("bearer")                   // bearer 키워드 자동 주입
                .bearerFormat("JWT")                // 포맷은 JWT
                .in(SecurityScheme.In.HEADER)       // 요청 헤더에 삽입
                .name(securityJwtName);

        // 전역 보안 요구사항 정의 (모든 API 엔드포인트 우측에 자물쇠 아이콘 표시)
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securityJwtName);

        return new OpenAPI()
                .components(new Components().addSecuritySchemes(securityJwtName, securityScheme))
                .addSecurityItem(securityRequirement)
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("EnjoyTrip API 명세서")
                .description("SSAFY 최종 관통 프로젝트 - 유저 및 여행지 관리 REST API")
                .version("1.0.0");
    }
}