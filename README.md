# 스케줄 관리 Todo 앱입니다.

## 진행 상황

- 백엔드 서버 구성 완료
  - 테스트 코드 작성 완료
  - Rest docs API 문서화 완료
- 프론트 페이지 개발 중 (30%)

# 1. 개요

​	제가 생각하는 기능의 일정 관리 앱이 없어서 직접 만들고 있습니다.

# 2. 디자인 및 기능

![image-20230813155015735](../../images/README/image-20230813155015735.png)

- TopList -> Todo -> DayPlan 으로 계층형 구조
  - TopList 를 달성하기 위한 Todo,
  - Todo 를 달성하기 위한 DayPlan 으로 구성
- 중요도, 긴급성을 중심으로 한 Todo 구분
- D+2 까지의 상세한 DayPlan 보기 -> 이틀 후의 계획까지 알고 있어야 당일 업무가 된다고 생각
- 일주일 계획을 한 눈으로 보기 -> 일주일의 진행 흐름을 머리 속에 그려야 한다고 생각

# 3. 사용 기술

- Frontend : react, electron
- Backend : Spring
- Dev : AWS, git actions(ci/cd)

# 4. ERD

![image-20230813155332593](../../images/README/image-20230813155332593.png)

# 5. 테스트

- 총 197개 단위 테스트 진행

![image-20230813155720785](../../images/README/image-20230813155720785.png)

# 6. 고민 사항

나중에 리팩토링에 필요하다고 생각되는 부분을 적어놨습니다. 우선 기능을 완성한 후 추후에 고민 사항을 보면서 리팩토링 예정입니다.

1. API validtation 과 Service Validation 구분
   - 비즈니스적으로 검증이 필요한 부분은 어디인가?
2. 테스트의 문서화가 잘 되었는가? 읽을 수 있는 문서인가?
3. 코드 변경 시마다 많은 테스트 변경이 필요함 -> 어떻게 하면 수정에 유연한 테스트 코드가 될까?
4. 삭제 시 bulk 연산이 필요한가? (cascade 가 적절한가)
5. 연관관계가 적절한가 (단방향, 양방향) / 연관관계 메서드의 설정은 적절한가
6. 의미없는 Public 메서드가 있는가? (캡슐화) 
   - ex) `topListService` 클래스의 `verifiedTopList` 조회 -> `controller` 에서 그대로 엔티티를 받게 될 수 있음
