package com.example.application.views.daftarantrian;

import com.example.application.data.entity.DaftarAntrian;
import com.example.application.data.service.DaftarAntrianService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Daftar Antrian")
@Route(value = "daftarantrian/:daftarAntrianID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class DaftarantrianView extends Div implements BeforeEnterObserver {

    private final String DAFTARANTRIAN_ID = "daftarAntrianID";
    private final String DAFTARANTRIAN_EDIT_ROUTE_TEMPLATE = "daftarantrian/%s/edit";

    private final Grid<DaftarAntrian> grid = new Grid<>(DaftarAntrian.class, false);

    private TextField nik;
    private TextField nama;
    private TextField nmrTlpn;
    private DatePicker tanggalLahir;
    private TextField jenisKelamin;
    private TextField alamat;
    private TextField keluhan;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final BeanValidationBinder<DaftarAntrian> binder;

    private DaftarAntrian daftarAntrian;

    private final DaftarAntrianService daftarAntrianService;

    public DaftarantrianView(DaftarAntrianService daftarAntrianService) {
        this.daftarAntrianService = daftarAntrianService;
        addClassNames("daftarantrian-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("nik").setAutoWidth(true);
        grid.addColumn("nama").setAutoWidth(true);
        grid.addColumn("nmrTlpn").setAutoWidth(true);
        grid.addColumn("tanggalLahir").setAutoWidth(true);
        grid.addColumn("jenisKelamin").setAutoWidth(true);
        grid.addColumn("alamat").setAutoWidth(true);
        grid.addColumn("keluhan").setAutoWidth(true);
        grid.setItems(query -> daftarAntrianService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(DAFTARANTRIAN_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(DaftarantrianView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(DaftarAntrian.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.daftarAntrian == null) {
                    this.daftarAntrian = new DaftarAntrian();
                    binder.writeBean(this.daftarAntrian);
                    daftarAntrianService.update(this.daftarAntrian);
                    clearForm();
                    refreshGrid();
                    Notification.show("Data Berhasil Ditambah");
                    UI.getCurrent().navigate(DaftarantrianView.class);
                } else{
                    binder.writeBean(this.daftarAntrian);
                    daftarAntrianService.update(this.daftarAntrian);
                    clearForm();
                    refreshGrid();
                    Notification.show("Data Berhasil Diperbarui");
                    UI.getCurrent().navigate(DaftarantrianView.class);
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
                if (this.daftarAntrian == null) {

                    Notification.show("Belum ada data");

                } else{
                    binder.writeBean(this.daftarAntrian);
                    daftarAntrianService.delete(this.daftarAntrian.getId());
                    clearForm();
                    refreshGrid();
                    Notification.show("Data telah terhapus");
                    UI.getCurrent().navigate(DaftarantrianView.class);
                }


            } catch (ValidationException validationException) {
                Notification.show("sebuah pengecualian terjadi saat mencoba menyimpan detail daftar antrian.");
            }
        });
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> daftarAntrianId = event.getRouteParameters().get(DAFTARANTRIAN_ID).map(Long::parseLong);
        if (daftarAntrianId.isPresent()) {
            Optional<DaftarAntrian> daftarAntrianFromBackend = daftarAntrianService.get(daftarAntrianId.get());
            if (daftarAntrianFromBackend.isPresent()) {
                populateForm(daftarAntrianFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested daftarAntrian was not found, ID = %s", daftarAntrianId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(DaftarantrianView.class);
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
        nik = new TextField("nik");
        nama = new TextField("nama");
        nmrTlpn = new TextField("nmrTlpn");
        tanggalLahir = new DatePicker("tanggalLahir");
        jenisKelamin = new TextField("jenisKelamin");
        alamat = new TextField("alamat");
        keluhan = new TextField("keluhan");
        formLayout.add(nik, nama, nmrTlpn, tanggalLahir,jenisKelamin, alamat, keluhan);

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
        buttonLayout.add(save, cancel, delete);
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

    private void populateForm(DaftarAntrian value) {
        this.daftarAntrian = value;
        binder.readBean(this.daftarAntrian);

    }
}
