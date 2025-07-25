package dev.mikablondo.springaitest;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.ai.mistralai.MistralAiChatModel;

@Route("")
public class MainView extends VerticalLayout {

    public MainView(MistralAiChatModel chatModel) {
        var question = new TextField();
        question.addClassName("question-field");

        var ask = new Button("Pose ta question");
        ask.addClassName("ask-button");

        var answer = new Paragraph();
        answer.addClassName("answer-paragraph");

        ask.addClickListener(event -> {
            try {
                answer.setText(chatModel.call(question.getValue()));
            } catch (Exception e) {
                answer.setText("Une erreur est survenue lors de la pose de la question.");
            }
            question.clear();
        });

        add(
                new HorizontalLayout(question, ask),
                answer
        );
    }
}