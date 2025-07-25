package dev.mikablondo.springaitest;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import org.springframework.ai.mistralai.MistralAiChatModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Route("")
public class MainView extends VerticalLayout {

    private final List<Discussion> messages = new ArrayList<>();
    private final VerticalLayout titleLayout = new VerticalLayout();
    private final VerticalLayout emptyLayout = new VerticalLayout();
    private final VerticalLayout discussionLayout = new VerticalLayout();

    public MainView(MistralAiChatModel chatModel) {
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        AtomicBoolean dejaAffiche = new AtomicBoolean(false);

        var question = new TextArea();
        question.setPlaceholder("Posez votre question ici...");
        question.setWidth("700px");
        question.setHeight("150px");
        question.setMaxLength(1000);
        question.setAutoselect(true);
        question.setClearButtonVisible(true);
        question.setRequiredIndicatorVisible(true);
        question.addClassName("question-field");

        Icon arrowIcon = VaadinIcon.ARROW_RIGHT.create();
        var ask = new Button(arrowIcon);
        ask.getElement().setProperty("title", "Envoie la !");
        ask.addClassName("ask-button");

        discussionLayout.setWidthFull();
        discussionLayout.setAlignItems(Alignment.CENTER);

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