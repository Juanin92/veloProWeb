import { Injectable, ElementRef } from '@angular/core';
import ApexCharts from 'apexcharts';

@Injectable({
  providedIn: 'root'
})
export class ChartService {

  private chart: any;

  initChart(chartElement: ElementRef): void {
    const options = {
      series: [{ name: "", data: [] }],
      chart: { type: "bar", height: "100%", width: "100%" },
      plotOptions: {
        bar: {
          distributed: true,
          horizontal: false,
          columnWidth: "50%"
        }
      },
      colors: ['#008FFB', '#00E396', '#FEB019'],
      xaxis: {
        categories: [],
        labels: {
          style: {
            colors: '#fff',
            fontWeight: 'bold'
          }
        }
      },
      dataLabels: {
        enabled: true,
        style: {
          colors: ['#fff']
        }
      },
      title: {
        text: "",
        align: "center",
        style: {
          color: '#fff'
        }
      },
      tooltip: {
        theme: 'dark',
        style: {
          fontSize: '14px',
          background: '#333',
          color: '#fff'
        },
        x: {
          show: true,
          formatter: undefined
        },
        y: {
          formatter: (val: number) => `Ventas: ${val}`,
          title: {
            formatter: () => ''
          }
        }
      },
      legend: {
        show: false
      }
    };

    this.chart = new ApexCharts(chartElement.nativeElement, options);
    this.chart.render();
  }

  updateChart(serie: number[], categories: string[] = [], legend: string, title: string, formatter?: (value: number) => string): void {
    this.chart.updateOptions({
        series: [{ name: legend, data: serie }],
        xaxis: { categories: categories },
        title: { text: title, align: "center" },
        tooltip: {
            y: {
              formatter: (val: number) => formatter ? formatter(Number(val)) : val.toString() 
            }
          },
          dataLabels: {
            formatter: (val: number) => formatter ? formatter(Number(val)) : val.toString()
          }
      });
  }

  resetChart(chartElement: ElementRef): void {
    const options = {
      series: [{ name: "", data: [] }],
      chart: { type: "bar", height: "100%", width: "100%" },
      plotOptions: {
        bar: {
          distributed: true,
          horizontal: false,
          columnWidth: "50%"
        }
      },
      colors: ['#008FFB', '#00E396', '#FEB019'],
      xaxis: {
        categories: [],
        labels: {
          style: {
            colors: '#fff',
            fontWeight: 'bold'
          }
        }
      },
      dataLabels: {
        enabled: true,
        style: {
          colors: ['#fff']
        }
      },
      title: {
        text: "",
        align: "center",
        style: {
          color: '#fff'
        }
      },
      tooltip: {
        theme: 'dark',
        style: {
          fontSize: '14px',
          background: '#333',
          color: '#fff'
        },
        x: {
          show: true,
          formatter: undefined
        },
        y: {
          formatter: (val: number) => `Ventas: ${val}`,
          title: {
            formatter: () => ''
          }
        }
      },
      legend: {
        show: false
      }
    };

    this.chart = new ApexCharts(chartElement.nativeElement, options);
    this.chart.render();
  }
}