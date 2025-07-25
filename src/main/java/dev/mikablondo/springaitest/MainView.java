package dev.mikablondo.springaitest;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.ai.mistralai.MistralAiChatModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Route("")
public class MainView extends VerticalLayout {

    private final List<Discussion> messages = new ArrayList<>();
    private final VerticalLayout emptyLayout = new VerticalLayout();
    private final VerticalLayout discussionLayout = new VerticalLayout();

    public MainView(MistralAiChatModel chatModel) {
        AtomicBoolean dejaAffiche = new AtomicBoolean(false);

        var question = new TextField();
        question.addClassName("question-field");

        var ask = new Button("Pose ta question");
        ask.addClassName("ask-button");

        ask.addClickListener(event -> {
            try {
                if (question.isEmpty()) {
                    if(!dejaAffiche.get()) {
                        emptyLayout.addComponentAtIndex(0, new Paragraph("Veuillez poser une question."));
                    }
                    dejaAffiche.set(true);
                    return;
                }

                emptyLayout.removeAll();

                String questionText = question.getValue();
                String response = chatModel.call(questionText);

                Discussion discussion = new Discussion("Question posée :", questionText, "Réponse de l'IA :", response);
                messages.add(discussion);

                Paragraph q = new Paragraph(discussion.titleQuestion() + " " + discussion.question());
                Paragraph r = new Paragraph(discussion.titleAnswer() + " " + discussion.answer());
                VerticalLayout messageBlock = new VerticalLayout(q, r);
                messageBlock.addClassName("message-block");

                // ajout du nouveau message au début de la discussion
                discussionLayout.addComponentAtIndex(0, messageBlock);
            } catch (Exception e) {
                discussionLayout.addComponentAtIndex(0, new Paragraph("❌ Une erreur est survenue."));
            }
            question.clear();
        });

        add(
                emptyLayout,
                new HorizontalLayout(question, ask),
                discussionLayout
        );
    }
}