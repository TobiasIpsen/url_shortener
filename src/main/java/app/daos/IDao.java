package app.daos;

import app.dtos.UrlDTO;
import app.entities.Url;

import java.util.List;

public interface IDao {

    public List<UrlDTO> getAll();
    public UrlDTO getLongUrl(String shortUrl);
    public UrlDTO create(UrlDTO urlDTO);
    public void delete(String shorturl);
    public UrlDTO update();
}
