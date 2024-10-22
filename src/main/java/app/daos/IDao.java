package app.daos;

import app.dtos.UrlDTO;
import app.entities.Url;

import java.util.List;

public interface IDao {

    public List<UrlDTO> getALl();
    public UrlDTO getLongUrl(String shortUrl);
    public UrlDTO create(UrlDTO urlDTO);
    void delete(String shorturl);
    public UrlDTO update();

}
