package app.daos;

import app.dtos.UrlDTO;
import app.dtos.UserDTO;
import app.entities.Url;

import java.util.List;

public interface IDao {

    public List<UrlDTO> getAll();
    public UrlDTO getLongUrl(String shortUrl);
    public UrlDTO create(UrlDTO urlDTO, UserDTO userDTO);
    public void delete(String shorturl);
    public UrlDTO update();
}
