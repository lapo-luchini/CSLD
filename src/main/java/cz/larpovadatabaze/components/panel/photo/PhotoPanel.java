package cz.larpovadatabaze.components.panel.photo;

import cz.larpovadatabaze.components.common.fileupload.FileUploadComponentPanel;
import cz.larpovadatabaze.components.common.fileupload.IFileUploadCallback;
import cz.larpovadatabaze.components.common.gallery.GalleryPanel;
import cz.larpovadatabaze.components.common.gallery.IGalleryDataProvider;
import cz.larpovadatabaze.components.common.gallery.IGalleryManager;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Photo;
import cz.larpovadatabaze.services.FileService;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.PhotoService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.upload.FileItem;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Panel for game photos
 *
 * User: Michal Kara
 * Date: 25.12.13
 * Time: 15:21
 */
public class PhotoPanel extends Panel {

    @SpringBean
    private GameService gameService;

    @SpringBean
    private PhotoService photoService;

    @SpringBean
    private FileService fileService;

    private WebMarkupContainer wrapper;

    private static class PhotoImageInfo extends GalleryPanel.ImageInfo {
        private final int orderSeq;

        public PhotoImageInfo(int id, String description, Boolean isTitle, int fullWidth, int fullHeight, int orderSeq) {
            super(id, description, isTitle, fullWidth, fullHeight);
            this.orderSeq = orderSeq;
        }
    }

    /**
     * Model to access photos of the game
     */
    private class PhotoListModel extends LoadableDetachableModel<List<? extends GalleryPanel.ImageInfo>> {
        @Override
        public List<? extends GalleryPanel.ImageInfo> load() {
            // Create info from game photos
            List<PhotoImageInfo> res = new ArrayList<PhotoImageInfo>();

            // Create intermediate objects
            for(Photo p : ((Game)getDefaultModelObject()).getPhotos()) {
                res.add(new PhotoImageInfo(p.getId(), p.getDescription(), null, p.getFullWidth(), p.getFullHeight(), p.getOrderSeq()));
            }

            // Sort
            Collections.sort(res, new Comparator<PhotoImageInfo>() {
                @Override
                public int compare(PhotoImageInfo o1, PhotoImageInfo o2) {
                    if (o1.orderSeq < o2.orderSeq) return -1;
                    if (o1.orderSeq > o2.orderSeq) return 1;
                    if (o1.getId() < o2.getId()) return -1;
                    if (o2.getId() > o2.getId()) return 1;
                    return 0;
                }
            });

            return res;
        }
    }

    /**
     * Gallery provider for game photos
     */
    private class PhotoGalleryDataProvider implements IGalleryDataProvider {

        @Override
        public AbstractResource.ResourceResponse getImagePreview(int photoId) {
            // Load photo
            Photo p = photoService.get(photoId);

            // Try if preview exists
            File f = fileService.getFilePreviewInDataDir(p.getImage().getPath());
            if (!f.exists()) {
                // Use full image
                f = fileService.getPathInDataDir(p.getImage().getPath());
            }
            return fileService.respondWithFile(f, p.getImage().getContentType());
        }

        @Override
        public AbstractResource.ResourceResponse getFullImage(int photoId) {
            // Load photo
            Photo p = photoService.get(photoId);

            // Send full path
            File f = fileService.getPathInDataDir(p.getImage().getPath());
            return fileService.respondWithFile(f, p.getImage().getContentType());
        }
    }

    /**
     * Provide gallery management functions
     */
    private class PhotoGalleryManager implements IGalleryManager {
        @Override
        public void setImageDescription(int imageId, String newDescription) {
            Photo p = photoService.get(imageId);
            p.setDescription(newDescription);
            photoService.saveOrUpdate(p);
        }

        @Override
        public void deleteImage(int imageId) {
            Photo p = photoService.get(imageId);
            photoService.remove(p);
        }

        @Override
        public void setImageOrder(Iterable<Integer> imageIds) {
            Game g = (Game)getDefaultModelObject();

            // Build map by id
            Map<Integer, Photo> photoMap = new HashMap<Integer, Photo>();
            for(Photo p : g.getPhotos()) photoMap.put(p.getId(), p);

            // Build new list
            List<Photo> newPhotos = new ArrayList<Photo>();
            for(Integer id : imageIds) {
                Photo p = photoMap.get(id);
                if (p != null) {
                    // Photo exists - set new order when it is different
                    int newOrder = newPhotos.size();
                    if (p.getOrderSeq() != newOrder) p.setOrderSeq(newOrder);
                    newPhotos.add(p);

                    photoMap.remove(id); // Remove from map
                }
            }

            // Add remaining photos
            newPhotos.addAll(photoMap.values());

            // Set and save
            g.setPhotos(newPhotos);
            gameService.saveOrUpdate(g);
        }
    }

    public PhotoPanel(String id, IModel<Game> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Game game = (Game)getDefaultModelObject();
        boolean canEdit = gameService.canEditGame(game);

        wrapper = new WebMarkupContainer("wrapper"); // Wrapper so that we can refresh ourselves
        wrapper.setOutputMarkupId(true);
        add(wrapper);

        /* Gallery */

        // Add gallery
        GalleryPanel gallery = new GalleryPanel("galleryPanel", new PhotoListModel(), new PhotoGalleryDataProvider(), canEdit?new PhotoGalleryManager():null);
        wrapper.add(gallery);

        /* Upload panel */
        if (canEdit) {
            // Create real upload panel
            wrapper.add(new FileUploadComponentPanel("uploadPanel", 5000000, "/(\\.|\\/)(gif|jpe?g|png)$/i", new IFileUploadCallback() {
                @Override
                public void filesUploaded(List<FileItem> fileItems) throws IOException {
                    for(FileItem fi : fileItems) {
                        photoService.createNewPhotoForGame((Game)PhotoPanel.this.getDefaultModelObject(), fi);
                    }
                }

                @Override
                public void fileUploadDone(AjaxRequestTarget target) {
                    // Refresh complete panel
                    target.add(wrapper);
                }
            }));

        }
        else {
            // Empty upload panel
            wrapper.add(new WebMarkupContainer("uploadPanel"));
        }
    }
}
