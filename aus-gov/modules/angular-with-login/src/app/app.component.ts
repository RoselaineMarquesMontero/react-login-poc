import { Component, Inject, OnInit } from '@angular/core';
import { Person } from '../model/person.model';
import HttpService from '../service/http.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{

	constructor(@Inject(HttpService) private httpService: HttpService) {}

	accessToken:string="";

	ngOnInit(): void {
		this.httpService.accessToken().subscribe(
			response => {
				this.accessToken = response.access_token;
				console.warn("Calling servlet to get access token from OnInit... acessToken: " + this.accessToken);
			}
		)
	}

	person: Person = {
		person_id: "Press the button to retrieve the Id",
		firstName: "Press the button to retrieve the Name"
	};

	makeApiCall():void {
		    console.warn('Calling the API with the Access Token ->', this.accessToken)
			this.httpService.getUser(this.accessToken).subscribe(responseBody => {
			this.person = responseBody;
			console.log(responseBody);
	  });
	}
}
