package com.sidney.banking.auth;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.sidney.banking.auth.service.AuthService;

@SpringBootTest
@AutoConfigureMockMvc
class AdminUserAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    /**
     * Um cliente autenticado não pode consultar todos os usuários.
     * O resultado esperado é 403 Forbidden.
     */
    @Test
    @WithMockUser(
            username = "cliente@banking.com",
            roles = "CUSTOMER"
    )
    void shouldDenyCustomerAccessToAdminUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(authService);
    }

    /**
     * Um administrador autenticado pode acessar o endpoint.
     * O resultado esperado é 200 OK.
     */
    @Test
    @WithMockUser(
            username = "admin@banking.com",
            roles = "ADMIN"
    )
    void shouldAllowAdminAccessToAdminUsers() throws Exception {
        when(authService.listUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    /**
     * Uma requisição sem autenticação deve ser recusada.
     * O resultado esperado é 401 Unauthorized.
     */
    @Test
    void shouldDenyUnauthenticatedAccessToAdminUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(authService);
    }
}