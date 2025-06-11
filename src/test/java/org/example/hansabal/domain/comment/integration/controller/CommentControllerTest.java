package org.example.hansabal.domain.comment.integration.controller;

import org.example.hansabal.domain.comment.controller.CommentController;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class CommentControllerTest {

}
