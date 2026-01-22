public interface StorageService {

    StorageCreateVO createStorage(StorageCreateDTO dto);

    List<StorageListItemVO> listMyStorage();
}
