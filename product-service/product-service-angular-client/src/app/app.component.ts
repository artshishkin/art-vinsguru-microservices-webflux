import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";

import {ProductService} from "./shared/product.service";
import {Product} from "./shared/product.model";
import {tap} from "rxjs/operators";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'product-service-angular-client';

  subs: Subscription[] = [];

  products: Product[] = [];

  constructor(private productService: ProductService) {
  }

  ngOnInit(): void {
    const sub = this.productService.getProducts(30)
      .pipe(tap(product => console.log(product)))
      .subscribe(product => this.products.push(product));
    this.subs.push(sub);
  }

  ngOnDestroy(): void {
    this.subs.forEach(sub => sub.unsubscribe());
  }

}
