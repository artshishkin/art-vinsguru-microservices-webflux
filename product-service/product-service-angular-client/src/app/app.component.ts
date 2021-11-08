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

  private observeProductsSubscription: Subscription | null = null;

  products: Product[] = [];

  constructor(private productService: ProductService) {
  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
    if (this.observeProductsSubscription)
      this.observeProductsSubscription.unsubscribe();
  }

  observeProducts(maxPrice: number): void {

    if (this.observeProductsSubscription)
      this.observeProductsSubscription.unsubscribe();

    this.observeProductsSubscription = this.productService.getProducts(maxPrice)
      .pipe(tap(product => console.log(product)))
      .subscribe(product => this.products.push(product));
  }

}
