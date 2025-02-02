### 0202 - AI 모델 선정 및 학습

## CNN

**Convolutional Neural Network**

convolution layer 적용. sliding window

feature map 생성

ReLU로 비선형성 추가

Polling

Fully Connected Layer

Weight Sharing

## Transformer

whole sequence **parallel** processing

**Self-Attention**

Encoder-Decoder Structure

Positional Encoding - 순차구조가 아니므로, 임베딩 벡터에 주기함수를 기반으로 한 포지셔널 인코딩 벡터를 추가해 학습

## LSTM

Long Short-Term Memory

RNN의 한 종류.

장기 의존성, 기울기 소실 문제 해결을 위한 모델.

RNN의 정보 희석을 Cell State와 Gate 구조를 도입하여 극복.

1. **Cell State**
    1. 네트워크 전체에 걸쳐 정보 전달.
    2. 단순한 선형 연산으로 정보 변형 X, 의존성 보존 가능
2. **Gates**
Sigmoid 사용
    1. **Forget Gate**
    이전 셀 상태의 어떤 정보를 버릴지 결정.
    2. **Input Gate**
    새로운 정보를 셀 상태에 얼마나 추가할지 결정
    3. **Output Gate**
    셀 상태를 기반으로 어떤 정보를 출력할지 결정

https://en.wikipedia.org/wiki/Long_short-term_memory

https://dgkim5360.tistory.com/entry/understanding-long-short-term-memory-lstm-kr

## ResNet

이전 degration (deep network learning error) 문제, 기울기 소실/폭발 문제를 해결하기 위한 모델

Skip Connection, Residual Connection 적용하여 해결.

**Residual Connection**
입력 $x$으로 부터 원하는 출력 $H(x)$을 직접 학습하는 대신, 잔차 함수 $F(x)$를 학습하도록 함.

$H(x) = F(x) + x$

입력과 출력 값이 크게 다르지 않다면 $F(x)$는 0에 근사하여, 잔여만 학습하면 되므로 최적화가 쉬워짐. 

**Skip Connection**
레이어의 입력을 블록의 출력에 더해, 정보와 기울기가 deep layer 까지 전달되도록 함.

**Identity Mapping**
입력과 출력의 차원이 동일하면 단순히 더함, 아니면 1x1 컨볼루션 등 사용.

**Bottleneck Block**
ResNet-50, 101, 152 같이 깊은 네트워크에서 연산량을 줄이기 위해 사용.
1x1 컨볼루션으로 채널 축소 → 3x3 컨볼루션으로 공간 정보 처리 → 1x1 컨볼루션으로 차원 복원

https://en.wikipedia.org/wiki/Residual_neural_network

https://ganghee-lee.tistory.com/41

## VGG

Visual Geometry Group에서 발표한 CNN 모델.

VGG16, VGG19 (컨볼루션 레이어의 수)

VGG16 (D) : 13개의 Convolution Layer, 3개의 Fully Connected Layer로 구성

VGG19 (E) : 16개의 Convolution Layer, 3개의 Fully Connected Layer로 구성

⇒ ImageNet의 1000개의 클래스 분류 가능.

3x3 filter, 1 stride 사용.

단순하고 일관된 구조, 비선형성 증가.

연산량, 메모리 요구량 많음.

https://en.wikipedia.org/wiki/VGGNet

`https://daechu.tistory.com/10

## Neural A* Search

A* Search의 휴리스틱 값을 예측하도록 학습.

전체 시스템을 E2E 학습

최적해 특성을 유지하며, 신경망 기반 추론과 결합.

https://omron-sinicx.github.io/neural-astar/

https://colab.research.google.com/github/omron-sinicx/neural-astar/blob/minimal/notebooks/example.ipynb

## MoblieNetV2

경량화된 CNN 모델.

활성화 함수 대신, 선형 활성화 사용.

**Depthwise Separable Convolution**

- 1x1 Convolution으로 입력 채널 수 확장.
- 각 채널별로 독립적인 3x3 depthwise convolution 수행.
- 1x1 convolution으로 차원을 줄여 bottleneck 형성.

**Skip Connection**

---

## Canny Edge

경계선 검출 알고리즘

- Noise Reduction
Gaussian filter를 통해 노이즈 제거
- Gradient Computation
Sobel filter를 통해 강도 변화/방향 계산
- Non-Maximum Suppression
가장 강한 값만 남기기.
- Double Thresholding
강한 엣지, 약한 엣지 구분
- Edge Tracking by Hysteresis
약한 엣지 판단

https://en.wikipedia.org/wiki/Canny_edge_detector

## 동적 시간 왜곡

Dynamic Time Warping

두 시간에 따른 데이터 간의 유사성을 측정하기 위한 알고리즘.

비선형 정렬

비용 행렬 구성

DP를 통한 최적 경로 찾기.

유사도 및 거리 계산.

## HMM 기반 매칭

Hidden Markov Model.

데이터 시퀸스와 숨겨진 상태 간의 관계를 모델링하고, 최적의 매칭 경로를 찾는 방법.

시계열 및 순차 데이터 비교

최적 상태 시퀸스 추정
- Viterbi 알고리즘

학습 및 파라미터 추정
- EM 기법(Baun-Welch 알고리즘)

---

# 개발 시작……

## 1. 설계…

일단 GPU 노트북을 받은 만큼… 할 건 해야지 마인드.

TensorFlow / Keras를 사용해서 사용자가 그린 그림을 입력으로 받고,
Transformer 모델을 통해 보행자가 다닐 수 있는 경로를 예측하는 AI를 만들 예쩡이다.

1. 간단한 CNN을 이용해 입력 이미지를 처리하고, 공간 차원을 시퀸스로 변환.
2. 여러 개의 Transformer 디코더 블록을 쌓아, 이미지 인코더의 특징 시퀸스를 기반으로 좌표 시퀸스를 생성.
3. 디코더의 각 시점마다 좌표를 예측하는 회귀 문제로 처리.
4. 간단한 합성 데이터를 만들어 모델 학습.
5. 학습된 모델로 한 샘플에 대해 좌표 시퀸스를 예측하고, 실제 테스트.

## 내가 생각하는 가장 큰 문제.

### → 어떻게 보행자가 갈 수 있는 길이라고 인식시키는가.

### 김성현식 생각

- 먼저 Transformer 모델을 활용하여, 한국의 지도에서 도보를 판단하도록 학습… (어캐함 1)
- 사용자의 그림을 도보 위에 올리도록 만들기 (어캐함 2)
- 좌표로 변환하기 (어캐함 3)

### + MapBox API를 통해 구현 가능한지 확인해보고 생각해야겠다.

좌표를 GeoJson으로 받고, 그 좌표들이 도보인지 판단하는 AI를 만들어봐야지…

이거 해보려고 AI 수업 다시보기로 봐야겠다 ㅜㅜ.


<hr>

## Keep
- AI 공부
- 적절한 체력 분배와 취침


## Problem
- 개발이 아니라 공부하는 느낌이어서 뭔가 성취감이 부족하다.


## Try
- AI 모델 구성 후 학습 데이터 만들기

