package dev.mikablondo.springaitest;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
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

@Route("")
public class MainView extends VerticalLayout {

    private final List<Discussion> messages = new ArrayList<>();
    private final MessageList messageList = new MessageList();
    private final Dialog dialog = new Dialog();

    public MainView(MistralAiChatModel chatModel) {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setSpacing(true);

        // titre
        H1 title = new H1("MiK@-GPT");
        title.getStyle()
                .set("color", "#FF6F61")
                .set("font-weight", "900")
                .set("font-size", "4rem")
                .set("text-align", "center")
                .set("margin", "40px 0 30px 0")
                .set("font-family", "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif")
                .set("text-shadow", "2px 2px 3px rgba(0,0,0,0.4), 4px 4px 6px rgba(0,0,0,0.3)");
        add(title);

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
                createEmptyDialog();
                return;
            }

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
                dialog,
                new HorizontalLayout(question, ask),
                messageList
        );
    }

    /**
     * Creates an empty dialog with a delete and cancel button.
     * This method is currently not used but can be used to create a dialog
     * for future enhancements.
     */
    private void createEmptyDialog() {
        dialog.removeAll();
        dialog.getFooter().removeAll();

        dialog.setHeaderTitle("C’est profond … comme une flaque en été !");

        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        dialog.getFooter().add(cancelButton);
        dialog.open();
    }
}
