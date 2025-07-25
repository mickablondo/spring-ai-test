package dev.mikablondo.springaitest;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import org.springframework.ai.mistralai.MistralAiChatModel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Route("")
public class MainView extends VerticalLayout {

    private final List<Discussion> messages = new ArrayList<>();
    private final VerticalLayout emptyLayout = new VerticalLayout();
    private final MessageList messageList = new MessageList();

    public MainView(MistralAiChatModel chatModel) {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setSpacing(true);

        AtomicBoolean dejaAffiche = new AtomicBoolean(false);

        var question = new TextArea();
        question.setPlaceholder("Posez votre question ici...");
        question.setWidth("700px");
        question.setHeight("150px");
        question.setMaxLength(1000);
        question.setAutoselect(true);
        question.setClearButtonVisible(true);
        question.setRequiredIndicatorVisible(true);

        Icon arrowIcon = VaadinIcon.ARROW_RIGHT.create();
        var ask = new Button(arrowIcon);
        ask.getElement().setProperty("title", "Envoyer !");
        ask.addClassName("ask-button");

        ask.addClickListener(event -> {
            if (question.isEmpty()) {
                if (!dejaAffiche.get()) {
                    emptyLayout.addComponentAtIndex(0, new Paragraph("Veuillez poser une question."));
                }
                dejaAffiche.set(true);
                return;
            }

            emptyLayout.removeAll();
            dejaAffiche.set(false);

            String questionText = question.getValue();
            String response;
            try {
                response = chatModel.call(questionText);
            } catch (Exception e) {
                response = "❌ Une erreur est survenue lors de l'appel à l'IA.";
            }

            messages.add(new Discussion(questionText, response));
            List<MessageListItem> items = new ArrayList<>();

            for (int i = messages.size() - 1; i >= 0; i--) {
                var m = messages.get(i);
                Instant now = Instant.now();

                var q = new MessageListItem(m.question(), now, "Vous");
                q.setUserColorIndex(1);

                var a = new MessageListItem(m.answer(), now, "IA");
                a.setUserColorIndex(2);

                items.add(q);
                items.add(a);
            }

            messageList.setItems(items);

            question.clear();
        });

        add(
                emptyLayout,
                new HorizontalLayout(question, ask),
                messageList
        );
    }
}
