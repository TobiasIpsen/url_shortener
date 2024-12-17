package app.daos;

import app.dtos.UrlDTO;
import app.dtos.UserDTO;

import java.util.List;

public interface IDao {

    public List<UrlDTO> getAll(UserDTO userDTO);
    public UrlDTO getLongUrl(String shortUrl);
    public UrlDTO create(UrlDTO urlDTO, UserDTO userDTO);
    public void delete(String shorturl);
    public UrlDTO updateLong(String shortUrl, UrlDTO urlDTO);
}
