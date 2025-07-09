import { Injectable, ElementRef } from '@angular/core';
import ApexCharts from 'apexcharts';

@Injectable({
  providedIn: 'root'
})
export class ChartService {

  private chart: any;

  /**
   * Inicializa un gráfico de barras en el elemento HTML proporcionado.
   * crea una instancia de ApexCharts y establecen opciones de diseño, colores, etiquetas y estilos.
   * @param chartElement - Elemento HTML donde se renderizar el gráfico.
   * 
   */
  initChart(chartElement: ElementRef): void {
    const options = {
      series: [{ name: "", data: [] }], // Se define una serie vacía al inicio.
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

  /**
   * Actualiza los datos y configuraciones del gráfico existente.
   * permite modificar el gráfico sin necesidad de reiniciarlo completamente.
   * @param serie - Datos de la serie para el gráfico.
   * @param categories - Datos mostrar en el eje X.
   * @param legend - Nombre de la serie (se mostrará en tooltips y leyenda si está habilitada).
   * @param title - Título del gráfico.
   * @param formatter - (Opcional) Función de formato para los valores
   */
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

  /**
   * Restablece el gráfico a su estado inicial, eliminando los datos y configuraciones personalizadas.
   * @param chartElement - Elemento HTML donde se renderizar el gráfico.
   */
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