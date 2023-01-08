package com.example.application.views.akunadmin;

import com.example.application.data.entity.AkunAdmin;
import com.example.application.data.service.AkunAdminService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Akun Admin")
@Route(value = "akunadmin/:akunAdminID?/:action?(edit)", layout = MainLayout.class)
public class AkunadminView extends Div implements BeforeEnterObserver {

    private final String AKUNADMIN_ID = "akunAdminID";
    private final String AKUNADMIN_EDIT_ROUTE_TEMPLATE = "akunadmin/%s/edit";

    private final Grid<AkunAdmin> grid = new Grid<>(AkunAdmin.class, false);

    private Upload gambar;
    private Image gambarPreview;
    private TextField nama;
    private TextField email;
    private TextField nomorTelepon;
    private TextField jenisKelamin;
    private TextField alamat;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private final BeanValidationBinder<AkunAdmin> binder;

    private AkunAdmin akunAdmin;

    private final AkunAdminService akunAdminService;

    public AkunadminView(AkunAdminService akunAdminService) {
        this.akunAdminService = akunAdminService;
        addClassNames("akunadmin-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        LitRenderer<AkunAdmin> gambarRenderer = LitRenderer.<AkunAdmin>of(
                "<span style='border-radius: 50%; overflow: hidden; display: flex; align-items: center; justify-content: center; width: 64px; height: 64px'><img style='max-width: 100%' src=${item.gambar} /></span>")
                .withProperty("gambar", item -> {
                    if (item != null && item.getGambar() != null) {
                        return "data:image;base64," + Base64.getEncoder().encodeToString(item.getGambar());
                    } else {
                        return "";
                    }
                });
        grid.addColumn(gambarRenderer).setHeader("Gambar").setWidth("96px").setFlexGrow(0);

        grid.addColumn("nama").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("nomorTelepon").setAutoWidth(true);
        grid.addColumn("jenisKelamin").setAutoWidth(true);
        grid.addColumn("alamat").setAutoWidth(true);
        grid.setItems(query -> akunAdminService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(AKUNADMIN_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(AkunadminView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(AkunAdmin.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        attachImageUpload(gambar, gambarPreview);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.akunAdmin == null) {
                    this.akunAdmin = new AkunAdmin();
                    binder.writeBean(this.akunAdmin);
                    akunAdminService.update(this.akunAdmin);
                    clearForm();
                    refreshGrid();
                    Notification.show("Data telah ditambahkan");
                    UI.getCurrent().navigate(AkunadminView.class);
                }else{
                    binder.writeBean(this.akunAdmin);
                    akunAdminService.update(this.akunAdmin);
                    clearForm();
                    refreshGrid();
                    Notification.show("Data telah diperbaharui");
                    UI.getCurrent().navigate(AkunadminView.class);
                }

            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Gagal memperbarui data");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Gagal memperbarui data");
            }
        });
        delete.addClickListener(e -> {
            try {
                if (this.akunAdmin == null) {

                    Notification.show("Data belum ada");

                }else{
                    binder.writeBean(this.akunAdmin);
                    akunAdminService.delete(this.akunAdmin.getId());
                    clearForm();
                    refreshGrid();
                    Notification.show("Data telah dihapus");
                    UI.getCurrent().navigate(AkunadminView.class);
                }

            } catch (ValidationException validationException) {
                Notification.show("Gagal menghapus data");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> akunAdminId = event.getRouteParameters().get(AKUNADMIN_ID).map(Long::parseLong);
        if (akunAdminId.isPresent()) {
            Optional<AkunAdmin> akunAdminFromBackend = akunAdminService.get(akunAdminId.get());
            if (akunAdminFromBackend.isPresent()) {
                populateForm(akunAdminFromBackend.get());
            } else {
                Notification.show(String.format("The requested akunAdmin was not found, ID = %s", akunAdminId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(AkunadminView.class);
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
        Label gambarLabel = new Label("Gambar");
        gambarPreview = new Image();
        gambarPreview.setWidth("100%");
        gambar = new Upload();
        gambar.getStyle().set("box-sizing", "border-box");
        gambar.getElement().appendChild(gambarPreview.getElement());
        nama = new TextField("Nama");
        email = new TextField("Email");
        nomorTelepon = new TextField("Nomor Telepon");
        jenisKelamin = new TextField("Jenis Kelamin");
        alamat = new TextField("Alamat");
        formLayout.add(gambarLabel, gambar, nama, email, nomorTelepon, jenisKelamin, alamat);

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

    private void attachImageUpload(Upload upload, Image preview) {
        ByteArrayOutputStream uploadBuffer = new ByteArrayOutputStream();
        upload.setAcceptedFileTypes("image/*");
        upload.setReceiver((fileName, mimeType) -> {
            uploadBuffer.reset();
            return uploadBuffer;
        });
        upload.addSucceededListener(e -> {
            StreamResource resource = new StreamResource(e.getFileName(),
                    () -> new ByteArrayInputStream(uploadBuffer.toByteArray()));
            preview.setSrc(resource);
            preview.setVisible(true);
            if (this.akunAdmin == null) {
                this.akunAdmin = new AkunAdmin();
            }
            this.akunAdmin.setGambar(uploadBuffer.toByteArray());
        });
        preview.setVisible(false);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(AkunAdmin value) {
        this.akunAdmin = value;
        binder.readBean(this.akunAdmin);
        this.gambarPreview.setVisible(value != null);
        if (value == null || value.getGambar() == null) {
            this.gambar.clearFileList();
            this.gambarPreview.setSrc("");
        } else {
            this.gambarPreview.setSrc("data:image;base64," + Base64.getEncoder().encodeToString(value.getGambar()));
        }

    }
}
