package dev.mikablondo.springaitest;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.ai.chat.ChatClient;

@Route("")
public class MainView extends VerticalLayout {

    public MainView(ChatClient chatClient) {
        var question = new TextField();
        var ask = new Button("Pose ta question");
        var answer = new Paragraph();

        ask.addClickListener(event -> {
           answer.setText(chatClient.call(question.getValue()));
        });

        add(
                new HorizontalLayout(question, ask),
                answer
        );
    }
}
