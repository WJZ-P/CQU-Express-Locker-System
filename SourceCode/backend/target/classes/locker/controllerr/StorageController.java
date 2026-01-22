@RestController
@RequestMapping("/api/v1/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    // 4.1 创建寄存订单
    @PostMapping("/create")
    public ApiResponse<StorageCreateVO> create(
            @RequestBody StorageCreateDTO dto) {
        return ApiResponse.success(storageService.createStorage(dto));
    }

    // 4.2 获取寄存列表
    @GetMapping("/list")
    public ApiResponse<List<StorageListItemVO>> list() {
        return ApiResponse.success(storageService.listMyStorage());
    }
}
