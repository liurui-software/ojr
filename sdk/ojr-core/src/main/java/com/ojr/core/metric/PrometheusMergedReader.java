package com.ojr.core.metric;

import io.opentelemetry.exporter.prometheus.PrometheusMetricReader;
import io.prometheus.metrics.model.registry.MultiCollector;
import io.prometheus.metrics.model.snapshots.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrometheusMergedReader implements MultiCollector {
    private static final Logger logger = Logger.getLogger(PrometheusMergedReader.class.getName());

    private final List<PrometheusMetricReader> readers = new ArrayList<>();

    public void registerReader(PrometheusMetricReader reader) {
        readers.add(reader);
    }

    @Override
    public MetricSnapshots collect() {
        Map<String, MetricSnapshot> snapMap = new HashMap<>();

        for (PrometheusMetricReader reader : readers) {
            MetricSnapshots snapshots = reader.collect();
            for (MetricSnapshot snapshot : snapshots) {
                if (snapshot == null)
                    continue;
                String name = snapshot.getMetadata().getName();
                if (snapMap.containsKey(name)) {
                    snapshot = merge(name, snapMap.get(name), snapshot);
                }
                snapMap.put(name, snapshot);
            }
        }

        return new MetricSnapshots(snapMap.values());
    }

    private MetricSnapshot merge(String name, MetricSnapshot snapshot1, MetricSnapshot snapshot2) {
        try {
            if (snapshot1 instanceof GaugeSnapshot) {
                return merge((GaugeSnapshot) snapshot1, (GaugeSnapshot) snapshot2);
            } else if (snapshot1 instanceof CounterSnapshot) {
                return merge((CounterSnapshot) snapshot1, (CounterSnapshot) snapshot2);
            } else if (snapshot1 instanceof HistogramSnapshot) {
                return merge((HistogramSnapshot) snapshot1, (HistogramSnapshot) snapshot2);
            } else if (snapshot1 instanceof SummarySnapshot) {
                return merge((SummarySnapshot) snapshot1, (SummarySnapshot) snapshot2);
            } else if (snapshot1 instanceof InfoSnapshot) {
                return merge((InfoSnapshot) snapshot1, (InfoSnapshot) snapshot2);
            } else if (snapshot1 instanceof StateSetSnapshot) {
                return merge((StateSetSnapshot) snapshot1, (StateSetSnapshot) snapshot2);
            } else if (snapshot1 instanceof UnknownSnapshot) {
                return merge((UnknownSnapshot) snapshot1, (UnknownSnapshot) snapshot2);
            } else {
                return snapshot1;
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Merge metrics failed at: " + name, e);
        }

        return snapshot1;
    }

    private GaugeSnapshot merge(GaugeSnapshot snapshot1, GaugeSnapshot snapshot2) {
        String name1 = snapshot1.getMetadata().getName();
        String help1 = snapshot1.getMetadata().getHelp();
        Unit unit1 = snapshot1.getMetadata().getUnit();
        GaugeSnapshot.Builder builder = GaugeSnapshot.builder().name(name1).help(help1).unit(unit1);
        List<GaugeSnapshot.GaugeDataPointSnapshot> dps = new ArrayList<>();
        dps.addAll(snapshot1.getDataPoints());
        dps.addAll(snapshot2.getDataPoints());
        for (GaugeSnapshot.GaugeDataPointSnapshot dp : dps) {
            builder.dataPoint(dp);
        }
        return builder.build();
    }

    private CounterSnapshot merge(CounterSnapshot snapshot1, CounterSnapshot snapshot2) {
        String name1 = snapshot1.getMetadata().getName();
        String help1 = snapshot1.getMetadata().getHelp();
        Unit unit1 = snapshot1.getMetadata().getUnit();
        CounterSnapshot.Builder builder = CounterSnapshot.builder().name(name1).help(help1).unit(unit1);
        List<CounterSnapshot.CounterDataPointSnapshot> dps = new ArrayList<>();
        dps.addAll(snapshot1.getDataPoints());
        dps.addAll(snapshot2.getDataPoints());
        for (CounterSnapshot.CounterDataPointSnapshot dp : dps) {
            builder.dataPoint(dp);
        }
        return builder.build();
    }

    private HistogramSnapshot merge(HistogramSnapshot snapshot1, HistogramSnapshot snapshot2) {
        String name1 = snapshot1.getMetadata().getName();
        String help1 = snapshot1.getMetadata().getHelp();
        Unit unit1 = snapshot1.getMetadata().getUnit();
        HistogramSnapshot.Builder builder = HistogramSnapshot.builder().name(name1).help(help1).unit(unit1);
        List<HistogramSnapshot.HistogramDataPointSnapshot> dps = new ArrayList<>();
        dps.addAll(snapshot1.getDataPoints());
        dps.addAll(snapshot2.getDataPoints());
        for (HistogramSnapshot.HistogramDataPointSnapshot dp : dps) {
            builder.dataPoint(dp);
        }
        return builder.build();
    }

    private SummarySnapshot merge(SummarySnapshot snapshot1, SummarySnapshot snapshot2) {
        String name1 = snapshot1.getMetadata().getName();
        String help1 = snapshot1.getMetadata().getHelp();
        Unit unit1 = snapshot1.getMetadata().getUnit();
        SummarySnapshot.Builder builder = SummarySnapshot.builder().name(name1).help(help1).unit(unit1);
        List<SummarySnapshot.SummaryDataPointSnapshot> dps = new ArrayList<>();
        dps.addAll(snapshot1.getDataPoints());
        dps.addAll(snapshot2.getDataPoints());
        for (SummarySnapshot.SummaryDataPointSnapshot dp : dps) {
            builder.dataPoint(dp);
        }
        return builder.build();
    }

    private InfoSnapshot merge(InfoSnapshot snapshot1, InfoSnapshot snapshot2) {
        String name1 = snapshot1.getMetadata().getName();
        String help1 = snapshot1.getMetadata().getHelp();
        //Unit unit1 = snapshot1.getMetadata().getUnit();
        InfoSnapshot.Builder builder = InfoSnapshot.builder().name(name1).help(help1); //.unit(unit1);
        List<InfoSnapshot.InfoDataPointSnapshot> dps = new ArrayList<>();
        dps.addAll(snapshot1.getDataPoints());
        dps.addAll(snapshot2.getDataPoints());
        for (InfoSnapshot.InfoDataPointSnapshot dp : dps) {
            builder.dataPoint(dp);
        }
        return builder.build();
    }

    private StateSetSnapshot merge(StateSetSnapshot snapshot1, StateSetSnapshot snapshot2) {
        String name1 = snapshot1.getMetadata().getName();
        String help1 = snapshot1.getMetadata().getHelp();
        Unit unit1 = snapshot1.getMetadata().getUnit();
        StateSetSnapshot.Builder builder = StateSetSnapshot.builder().name(name1).help(help1).unit(unit1);
        List<StateSetSnapshot.StateSetDataPointSnapshot> dps = new ArrayList<>();
        dps.addAll(snapshot1.getDataPoints());
        dps.addAll(snapshot2.getDataPoints());
        for (StateSetSnapshot.StateSetDataPointSnapshot dp : dps) {
            builder.dataPoint(dp);
        }
        return builder.build();
    }

    private UnknownSnapshot merge(UnknownSnapshot snapshot1, UnknownSnapshot snapshot2) {
        String name1 = snapshot1.getMetadata().getName();
        String help1 = snapshot1.getMetadata().getHelp();
        Unit unit1 = snapshot1.getMetadata().getUnit();
        UnknownSnapshot.Builder builder = UnknownSnapshot.builder().name(name1).help(help1).unit(unit1);
        List<UnknownSnapshot.UnknownDataPointSnapshot> dps = new ArrayList<>();
        dps.addAll(snapshot1.getDataPoints());
        dps.addAll(snapshot2.getDataPoints());
        for (UnknownSnapshot.UnknownDataPointSnapshot dp : dps) {
            builder.dataPoint(dp);
        }
        return builder.build();
    }

}
