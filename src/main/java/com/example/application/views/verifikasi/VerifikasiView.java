package com.example.application.views.verifikasi;

import com.example.application.data.entity.VerifikasiAntrian;
import com.example.application.data.service.VerifikasiAntrianService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Verifikasi Antrian")
@Route(value = "verifikasi/:verifikasiAntrianID?/:action?(edit)", layout = MainLayout.class)
public class VerifikasiView extends Div implements BeforeEnterObserver {

    private final String VERIFIKASIANTRIAN_ID = "verifikasiAntrianID";
    private final String VERIFIKASIANTRIAN_EDIT_ROUTE_TEMPLATE = "verifikasi/%s/edit";

    private final Grid<VerifikasiAntrian> grid = new Grid<>(VerifikasiAntrian.class, false);

    private TextField nik;
    private TextField nama;
    private TextField keluhan;
    private TextField diagnosa;
    private TextField dosisObat;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final BeanValidationBinder<VerifikasiAntrian> binder;

    private VerifikasiAntrian verifikasiAntrian;

    private final VerifikasiAntrianService verifikasiAntrianService;

    public VerifikasiView(VerifikasiAntrianService verifikasiAntrianService) {
        this.verifikasiAntrianService = verifikasiAntrianService;
        addClassNames("verifikasi-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("nik").setAutoWidth(true);
        grid.addColumn("nama").setAutoWidth(true);
        grid.addColumn("keluhan").setAutoWidth(true);
        grid.addColumn("diagnosa").setAutoWidth(true);
        grid.addColumn("dosisObat").setAutoWidth(true);
        grid.setItems(query -> verifikasiAntrianService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent()
                        .navigate(String.format(VERIFIKASIANTRIAN_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(VerifikasiView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(VerifikasiAntrian.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.verifikasiAntrian == null) {
                    this.verifikasiAntrian = new VerifikasiAntrian();
                    binder.writeBean(this.verifikasiAntrian);
                    verifikasiAntrianService.update(this.verifikasiAntrian);
                    clearForm();
                    refreshGrid();
                    Notification.show("Data telah terverifikasi");
                    UI.getCurrent().navigate(VerifikasiView.class);
                } else {
                    binder.writeBean(this.verifikasiAntrian);
                    verifikasiAntrianService.update(this.verifikasiAntrian);
                    clearForm();
                    refreshGrid();
                    Notification.show("Data telah diperbarui");
                    UI.getCurrent().navigate(VerifikasiView.class);
                }
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data.");
            }
        });

        delete.addClickListener(e -> {
            try {
                if (this.verifikasiAntrian == null) {

                    Notification.show("Belum ada data");

                } else {
                    binder.writeBean(this.verifikasiAntrian);
                    verifikasiAntrianService.delete(this.verifikasiAntrian.getId());
                    clearForm();
                    refreshGrid();
                    Notification.show("Data telah terhapus");
                    UI.getCurrent().navigate(VerifikasiView.class);
                }

            } catch (ValidationException validationException) {
                Notification.show("Sebuah pengecualian terjadi saat mencoba menyimpan detail verifikasi antrian");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> verifikasiAntrianId = event.getRouteParameters().get(VERIFIKASIANTRIAN_ID).map(Long::parseLong);
        if (verifikasiAntrianId.isPresent()) {
            Optional<VerifikasiAntrian> verifikasiAntrianFromBackend = verifikasiAntrianService
                    .get(verifikasiAntrianId.get());
            if (verifikasiAntrianFromBackend.isPresent()) {
                populateForm(verifikasiAntrianFromBackend.get());
            } else {
                Notification.show(String.format("The requested verifikasiAntrian was not found, ID = %s",
                        verifikasiAntrianId.get()), 3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(VerifikasiView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        nik = new TextField("NIK");
        nama = new TextField("Nama");
        keluhan = new TextField("Keluhan");
        diagnosa = new TextField("Diagnosa");
        dosisObat = new TextField("Dosis Obat");
        formLayout.add(nik, nama, keluhan, diagnosa, dosisObat);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(save, delete, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(VerifikasiAntrian value) {
        this.verifikasiAntrian = value;
        binder.readBean(this.verifikasiAntrian);

    }
}
