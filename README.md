Business​ ​Case​:
A Marketplace for Self-Employed. The marketplace allows employers to to
post jobs, while perspective self employed can bid for projects. 

In this system, we have two actors:
1. Seller​: Posts a project with detailed project requirements, such as description,
maximum budget and last day/time for accepting bids.
2. Buyer​ (Self-Employed): Bids for work on a fixed price.

High​ ​Level​ ​Requirements: 
  
1. REST API to support the following requirements:
a. Create a Project.
b. Get a Project by ID. Returned fields should include the lowest bid amount.
c. API to Bid for a Project
d. API to Query for all Open Projects.
2. The Buyer with the lowest bid automatically wins the bid when the deadline is reache

This is a maven spring boot application. Provided maven is installed in the system and available in the classpath :
Please follow the following steps to compile and run the project :-

1. mvn clean install
	This should run all the test cases, Code overage (Branch) is set to 80%. Current code coverage is 85% (Branch)
	
2. mvn spring-boot:run
	This should start the application. You will see a message similar to the below in the console :
	     Started JobMarketplaceApplication in 5.557 seconds (JVM running for 12.478)
	The application is up and running!

The application can be tested using any rest client. My personal preference is Postman.
Once the application is running, he H2 console is accessible at http://localhost:8080/console/

There is a microsoft doc/pdf named "Job Market" in the root directory of the folder that explains all the APIs , request and response structures.

The responses follow HATEOAS, every response will provide useful links for further information retrieval. 

The date time format is in UTC following ISO 8601 standard : "2018-09-09T12:00:00Z"

The project uses Spring data jpa. The entities need to be enhanced before usage. Maven open jpa enhancer plugin will automatically take care of enhancement. The project will not run if the enhancement hasnt been completed. Sometimes junit tests need enhancement as well, if the tests dont run individually, mvn clean install should be run in order to enhance all the entities.

User has to be created at first in order to proceed with other APIs, hence user creation API is listed at the beginning:

Main Apis :
1. Create User : POST /v1/persons
Sample Request
{
  "firstName": "Partha",
  "lastName": "Sanyal",
  "email": "partha.sanyal@email.com"
}

Sample Response:
{
    "personId": "42b285bd-d865-4952-b06e-9a7c0e1a8be5",
    "firstName": "Partha",
    "lastName": "Sanyal",
    "email": "partha.sanyal@email.com",
    "_links": {
        "self": {
            "href": "http://localhost:8080/v1/persons/42b285bd-d865-4952-b06e-9a7c0e1a8be5"
        },
        "allUsers": {
            "href": "http://localhost:8080/v1/persons"
        }
    }
}

2. GET /v1/persons (this is for providing an admin functionality)

3. GET /v1/persons/{person-id} to get specific user based on user id.

4. POST /v1/projects (Create Projects)
Sample Request :

{
    "projectName" : "project Name",
	"projectOwnerId" : "cfdb532f-a4b5-45ec-bc96-81c6fbb0649d",
	"requirements" : "project requirements",
	"maximumBudget" : 1000.0,
	"lastDateTime" : "2018-02-07T04:53:00Z"
}

Sample response :

{
    "projectId": "dfad2496-cdda-47b2-ad39-6465f7cf6dee",
    "projectName": "project Name",
    "projectOwnerId": "cfdb532f-a4b5-45ec-bc96-81c6fbb0649d",
    "requirements": "project requirements",
    "maximumBudget": 1000.0,
    "lastDateTime": "2018-02-07T04:53:00Z",
    "lowestBidAmount": 0,
    "_links": {
        "self": {
            "href": "http://localhost:8080/v1/projects/dfad2496-cdda-47b2-ad39-6465f7cf6dee"
        },
        "projectOwner": {
            "href": "http://localhost:8080/v1/persons/cfdb532f-a4b5-45ec-bc96-81c6fbb0649d"
        },
        "bids": {
            "href": "http://localhost:8080/v1/projects/dfad2496-cdda-47b2-ad39-6465f7cf6dee/bids"
        },
        "filterProjects": {
            "href": "http://localhost:8080/v1/projects"
        },
        "winningBid": {
            "href": "http://localhost:8080/v1/projects/dfad2496-cdda-47b2-ad39-6465f7cf6dee/winningBid"
        }
    }
}

The lowestBidAmount will be seen, once the bidders start posting bids for the project as below :

{
    "projectId": "794a1070-371c-42b0-8a1c-59b75242110f",
    "projectName": "project Name",
    "projectOwnerId": "6d0df965-fe04-4568-99e0-66afc8992958",
    "requirements": "project requirements",
    "maximumBudget": 1000,
    "lastDateTime": "2018-02-06T08:33:00Z",
    "lowestBidAmount": 390,
    "_links": {
        "self": {
            "href": "http://localhost:8080/v1/projects/794a1070-371c-42b0-8a1c-59b75242110f"
        },
        "projectOwner": {
            "href": "http://localhost:8080/v1/persons/6d0df965-fe04-4568-99e0-66afc8992958"
        },
        "bids": {
            "href": "http://localhost:8080/v1/projects/794a1070-371c-42b0-8a1c-59b75242110f/bids"
        },
        "filterProjects": {
            "href": "http://localhost:8080/v1/projects"
        },
        "winningBid": {
            "href": "http://localhost:8080/v1/projects/794a1070-371c-42b0-8a1c-59b75242110f/winningBid"
        }
    }
}
5.GET /v1/projects/{project-id} -> can be used to get the project details based on projectId

6.GET /v1/projects : Tells about the filter options available 

Sample Response:

{
    "_links": {
        "filter": {
            "href": "http://localhost:8080/v1/projects/filter{?type=ALL/OPEN/CLOSED,projectOwnerId}",
            "templated": true
        }
    }
}

7. GET /v1/projects/filter?type=OPEN : Retrieve all open projects

8. GET /v1/projects/filter?type=Closed : Retrieve all closed projects

9. GET /v1/projects/filter?type=All : Retrieve all projects (Admin functionality)

10. GET /v1/projects/filter?type=open&projectOwnerId=abc : Retrieve all open projects whose owner is a person with id "abc"

11. GET /v1/projects/filter?type=closed&projectOwnerId=abc : Retrieve all closed projects whose owner is a person with id "abc"

12. GET /v1/projects/filter?projectOwnerId=abc : Retrieve all projects whose owner is a person with id "abc"

Sample filter response :

[
    {
        "projectId": "e153cb23-b8c7-46db-8506-f88e60914926",
        "projectName": "project Name",
        "projectOwnerId": "6d0df965-fe04-4568-99e0-66afc8992958",
        "requirements": "project requirements",
        "maximumBudget": 1000,
        "lastDateTime": "2018-02-07T04:53:00Z",
        "lowestBidAmount": 0,
        "_links": {
            "self": {
                "href": "http://localhost:8080/v1/projects/e153cb23-b8c7-46db-8506-f88e60914926"
            },
            "projectOwner": {
                "href": "http://localhost:8080/v1/persons/6d0df965-fe04-4568-99e0-66afc8992958"
            },
            "bids": {
                "href": "http://localhost:8080/v1/projects/e153cb23-b8c7-46db-8506-f88e60914926/bids"
            },
            "filterProjects": {
                "href": "http://localhost:8080/v1/projects"
            },
            "winningBid": {
                "href": "http://localhost:8080/v1/projects/e153cb23-b8c7-46db-8506-f88e60914926/winningBid"
            }
        }
    }
]

13. Error responses are of standard format as below:

{
    "timestamp": "2018-02-06T00:23:51Z",
    "path": "http://localhost:8080/v1/persons",
    "errors": [
        {
            "code": "400",
            "message": "Duplicate contact found with the email address"
        }
    ]
}

14. GET /v1/projects/{project-id}/bids : Retrieves all the bids posted for the project with project id "project-id"
Sample response :
[
 {
    "bidId": "6f7140d7-911e-48b0-b491-d079dcb30695",
    "bidderId": "87c3857e-387a-4629-b2c3-abfe515a8dd4",
    "bidAmount": 900,
    "_links": {
        "self": {
            "href": "http://localhost:8080/v1/projects/e153cb23-b8c7-46db-8506-f88e60914926/bids/6f7140d7-911e-48b0-b491-d079dcb30695"
        },
        "bidder": {
            "href": "http://localhost:8080/v1/persons/87c3857e-387a-4629-b2c3-abfe515a8dd4"
        },
        "project": {
            "href": "http://localhost:8080/v1/projects/e153cb23-b8c7-46db-8506-f88e60914926"
        }
    }
 }
]

15. GET /v1/projects/{project-id}/bids/{bid-id} : Retrieve the bid with bid-id for a project with project-id

16 POST /v1/projects/{project-id}/bids Bid for a project
Sample Request :
{
	"bidderId" : "87c3857e-387a-4629-b2c3-abfe515a8dd4",
	"bidAmount" : 900
}

17. GET /v1/projects/{project-id}/winningBid : 

After the project is closed, and the winning bid schedule selector has successfully run, this link can be used to retrieve the winning bid. Note that the scheduler runs every 5 minutes and processes the projects within the time window of 10 minutes in the past to 5 minutes in the past. For example if the scheduler kicks of at 10:30p it will process closed projects between 10:20p and 10:30p to select the winning bid. If two bids with the same bid amount came in, the one which came earlier will be given preference.

Sample Response :

{
    "bidId": "9b42ee70-8f10-4494-ae4b-d8ad6c2cce4c",
    "bidderId": "87c3857e-387a-4629-b2c3-abfe515a8dd4",
    "bidAmount": 390,
    "_links": {
        "self": {
            "href": "http://localhost:8080/v1/projects/794a1070-371c-42b0-8a1c-59b75242110f/bids/9b42ee70-8f10-4494-ae4b-d8ad6c2cce4c"
        },
        "bidder": {
            "href": "http://localhost:8080/v1/persons/87c3857e-387a-4629-b2c3-abfe515a8dd4"
        },
        "project": {
            "href": "http://localhost:8080/v1/projects/794a1070-371c-42b0-8a1c-59b75242110f"
        }
    }
}

---------------------------------------------------------------------------------------------------------------------

Thanks,
Partha Pratim Sanyal


