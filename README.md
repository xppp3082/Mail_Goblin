<div align="center">
  <div style="display: flex; align-items: center;">
      <img src="https://github.com/xppp3082/Mail_Goblin/assets/64196497/d5fc5a60-16d2-46db-b6fe-8507445edbec" alt="mailGoblinIcon" width="100" height="100">
      <h1>Mail Goblin</h1>
  </div>

  ![Static Badge](https://img.shields.io/badge/SpringBoot-3.2.4-brightgreen?style=flat&logo=SpringBoot&logoColor=green&logoSize=%20auto&color=green)
  ![Static Badge](https://img.shields.io/badge/Java-17-orange?style=flat&logo=Java&logoColor=orange&logoSize=%20auto&color=orange)
  ![Static Badge](https://img.shields.io/badge/AWS-service-pink?style=flat&logo=amazonaws&logoColor=orange&logoSize=%20auto&color=%23FF007F)



</div>

## Description
Mail Goblin is a platform that assists marketers in tracking the performance of email campaigns. By utilizing pixel tracking, webhooks, and custom link tracking, it enables the tracking of every sent email, turning your emails into money-making machine!

## Tech Stack
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![Nginx](https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white) ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white) ![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens) ![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white) ![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white) ![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white) ![Ubuntu](https://img.shields.io/badge/Ubuntu-E95420?style=for-the-badge&logo=ubuntu&logoColor=white) ![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white) ![JavaScript](https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E) ![CSS3](https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white)  ![Bootstrap](https://img.shields.io/badge/bootstrap-%23563D7C.svg?style=for-the-badge&logo=bootstrap&logoColor=white) ![jQuery](https://img.shields.io/badge/jquery-%230769AD.svg?style=for-the-badge&logo=jquery&logoColor=white)   

## Test Accounts
| Email               | Password |
|-------------------------|--------------|
| test12345@example.com    | 123456   |


## Features
- **Template Editor :**<br>
  Mail Goblin offers a personalized email template setup feature, allowing you to easily manage all your email templates !
  
- **Campaign Automation :**<br>
  Experience our cutting-edge Campaign Automation feature for unparalleled efficiency,revolutionizing the way you manage and execute marketing strategies.
    
- **Audience Management :**<br>
  Mail Goblin provides a one-stop audience management service,
  helping you identify audiences interested in your product and ensuring your money is spent wisely.
  
- **Dashboard for Mail Tracking :**
  Mail Goblin provides precise email tracking capabilities,
  ensuring all emails sent through Mail Goblin remain traceable,
  enabling you to accurately analyze the performance of each marketing campaign.
  
## How to Use
### Create A Mail Template
![Mail_Template](https://github.com/xppp3082/Mail_Goblin/assets/64196497/84a7886d-c8c7-4e07-9cc4-f59e4cca6d91)

### Create Tag for Labeling Audience
![Add_Tag](https://github.com/xppp3082/Mail_Goblin/assets/64196497/840308f9-aff5-4530-93a7-af6196336526)

### Target Audience by Tag
![Audience](https://github.com/xppp3082/Mail_Goblin/assets/64196497/dee75ef4-1ceb-4e8a-a9c4-296034193680)

### Create Campaign Base on Template
![Campaign](https://github.com/xppp3082/Mail_Goblin/assets/64196497/9c9487d1-ffca-48a2-963b-ea756affaed4)

### Tracking Mails by Dashboard
![Dashboard](https://github.com/xppp3082/Mail_Goblin/assets/64196497/c915ee4f-079c-46bc-be1a-aca2ffa18140)


## Architecture
![image](https://github.com/xppp3082/Mail_Goblin/assets/64196497/0feab6a1-1d41-4640-95a0-7172b15ff4da)


## Technique
### Framework
- Spring Boot
### Database
- MySQL
- Redis
### CloudService
- Elastic Compute Cloud(EC2)
- Relational Database Service(RDS)
- ElastiCache
- Simple Queue Service(SQS)
- Simple Notification Service(SNS)
- CloudWatch
- Simple Cloud Storage(S3)
- CloudFront
- Route 53
- Auto Scaling Group

### Technologies Appliment
- Developed the backend using the **Java Spring Boot framework**, along with frontend development using **JavaScript**, **HTML**, and **CSS**.
- Tracked the status of all sent emails through **Webhook**, **Pixel Tracking**, and **Custom Link Tracking**.
- Deploy the project on AWS cloud infrastructure including **RDS**, **ElastiCache**, **S3**, **CloudFront**, **SQS**, **CloudWatch**, and **SNS**.
- Implemented a provider-consumer architecture by using **SQS** for efficient mass email campaign delivery.
- Utilize **Redis** as an intermediary for information updates, using scheduled batch updates to insert data to **RDS**, avoiding **race conditions** among different data sources.
- Designed **AMI**, **Launch templates**, and an **Auto Scaling group** for the **consumer**, ensuring the stability of campaign deliveries
- Use **UTC time** conversion for backend time processing convert time to the user's local time zone in the frontend.
- Monitored email delivery status in detail using **custom metrics** through **AWS CloudWatch**. Automated email notifications via SNS are triggered upon detecting sending failures.
- Integrated **HighCharts** to develop interactive tracking dashboards, enabling visualization of marketing data.

## Contact
🧑‍💻 Travis Liu   
:mailbox: xppp3082@gmail.com
