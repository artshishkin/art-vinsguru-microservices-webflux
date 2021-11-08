import {Injectable, NgZone} from '@angular/core';
import {Observable, Observer} from "rxjs";

import {SseService} from "./sse.service";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private productsUrl = environment.productsUrl;

  constructor(private zone: NgZone, private sseService: SseService) {
  }

  getProducts(): Observable<any> {
    return Observable.create((observer: Observer<any>) => {
      const eventSource = this.sseService.getEventSource(this.productsUrl);

      eventSource.onmessage = event => {
        this.zone.run(() => observer.next(event));
      };

      eventSource.onerror = error => {
        this.zone.run(() => observer.error(error));
      };

    });
  }
}
