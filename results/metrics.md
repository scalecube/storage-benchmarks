**Conditions**
_Pre-populate_: 5M records
_Reads_: Read random item from 5M
_Writes_: Write 5M records from each thread (4 threads = num of cores)
_Hardware_: Amazon EC2 with SSD g2
---

| DB| Reads(ns/op) | Reads(calls/second) | Writes(ns/op) | Writes(calls/second) |
| --- | --- | --- | --- | --- | 
| Chronicle 2cpu | 3429 | 102889 | 5430 | 52267 |
| Chronicle 4cpu | 6392 | 223274 | 5780 | 92596 |
| RocksDB 2cpu | 20233 | 49932 | 12442 | 39937 |
| RocksDB 4cpu | 19116 | 120848 | 13629 | 74184 |
| LMDB 2cpu | 5371 | 78326 | 32636 | 28654 |
| LMDB 4cpu | 5752 | 193882 | 42223 | 48693 |

![image](https://user-images.githubusercontent.com/1706296/39830346-d6e81466-53c9-11e8-92fc-e309b51b3f08.png)
