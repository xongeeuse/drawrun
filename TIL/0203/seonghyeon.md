### 0203 - AI 모델 선정 및 학습

# AI 학습을 위한 로컬 서버 구축

# 제공받은 노트북에 GPU AI 서버 구축

### 1. 노트북 버전 확인

- GPU : Geforce RTX 3050 Ti Laptop GPU
- Window : window 11 enterprise

### 2. NVIDIA 드라이버 설치

https://www.nvidia.com/ko-kr/drivers/

버전에 맞게 설치

2025.02.03 기준 → GeForce Game Ready 드라이버 572.16 | Window 11

```purescript
nvidia-smi
```

CUDA Version: 12.8

### 3. GPU의 Compute Capability 확인

Geforce RTX 3050 Ti Laptop GPU

→ 8.6

### 4. Compute Capability에 해당하는 CUDA Version 확인

8.6

→ 11.1 ~ 12.8

Pytorch에 맞춰 12.6 설치

### 5. Pytorch 설치 버전 확인하기

2.6.0 (Last Stable)

### 6. CUDA Toolkit 설치

https://developer.nvidia.com/cuda-downloads

Cuda Toolkit 12.6.3

**설치 실패**

- NsightVSE 해제 후 설치 → 실패
- NsightCompute,  NsightVSE 해제 후 설치 → 성공 후 재시작 → 실패
- Visual Stdio Community 2022 17.12.4 설치
- Nvidia Toolkit 설치 성공…

```purescript
nvcc -V
```

### 7. NVIDIA Developer 회원가입

developer.nvidia.com

### 8. cuDNN 설치

developer.nvidia.com/rdp/cudnn-archive

cuDNN v8.9.7 설치

### 9. cuDNN 파일을 Cuda Toolkit 폴더에 덮어쓰기.

### 10. 환경 변수 확인하기.

```purescript
CUDA_PATH
CUDA_PATH_V12_8
```

### 11. Python 설치하기

python 3.13.1

### 12. Pytorch 설치하기

Compute Platform CUDA 12.6

# 이제는 이걸로 뭘 해야할지 고민할 시기.
1. 지도의 이미지를보고 보행자 경로를 학습.
2. 시작 좌표와 **강아지** 모양의 길을 학습.
3. 길을 좌표의 배열로 변환...

<hr>

## Keep
- 꾸준한 카페인 섭취...?
- 팀원이 모두 희망을 보여줘서, 나도 끝까지 달리기.


## Problem
- 생각보다 촉박한 기간.


## Try
- 일단 모델 만들고 무작정 학습시키기.

