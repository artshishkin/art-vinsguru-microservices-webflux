import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";

import {ProductService} from "./shared/product.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'product-service-angular-client';

  subs: Subscription[] = [];

  constructor(private productService: ProductService) {
  }

  ngOnInit(): void {
    const sub = this.productService.getProducts().subscribe(data => console.log(data));
    this.subs.push(sub);
  }

  ngOnDestroy(): void {
    this.subs.forEach(sub => sub.unsubscribe());
  }

}
