
import DDS.*;
import OpenDDS.DCPS.*;
import org.omg.CORBA.StringSeqHolder;
import Nexmatix.*;
import java.io.PrintStream;


public class SubValve
{
  private static final int VALVE_PARTICIPANT = 23;
  private static final String VALVE_TOPIC = "Valve";

  
  public static boolean checkReliable(String[] paramArrayOfString)
  {
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if (paramArrayOfString[i].equals("-r")) {
        return true;
      }
    }
    return false;
  }
  
  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    System.out.println("Start Subscriber");
    boolean reliable = checkReliable(paramArrayOfString);
    
    DomainParticipantFactory localDomainParticipantFactory = TheParticipantFactory.WithArgs(new StringSeqHolder(paramArrayOfString));
    if (localDomainParticipantFactory == null)
    {
      System.err.println("ERROR: Domain Participant Factory not found");
      return;
    }
    DomainParticipant localDomainParticipant = localDomainParticipantFactory.create_participant(VALVE_PARTICIPANT, PARTICIPANT_QOS_DEFAULT.get(), null, -1);
    if (localDomainParticipant == null)
    {
      System.err.println("ERROR: Domain Participant creation failed");
      return;
    }
    ValveStatusTypeSupportImpl localValveStatusTypeSupportImpl = new ValveStatusTypeSupportImpl();
    if (localValveStatusTypeSupportImpl.register_type(localDomainParticipant, "") != 0)
    {
      System.err.println("ERROR: register_type failed");
      return;
    }
    Topic localTopic = localDomainParticipant.create_topic(VALVE_TOPIC, localValveStatusTypeSupportImpl
      .get_type_name(), 
      TOPIC_QOS_DEFAULT.get(), null, -1);
    if (localTopic == null)
    {
      System.err.println("ERROR: Topic creation failed");
      return;
    }
    Subscriber localSubscriber = localDomainParticipant.create_subscriber(SUBSCRIBER_QOS_DEFAULT.get(), null, -1);
    if (localSubscriber == null)
    {
      System.err.println("ERROR: Subscriber creation failed");
      return;
    }
    DataReaderQos localDataReaderQos = new DataReaderQos();
    localDataReaderQos.durability = new DurabilityQosPolicy();
    localDataReaderQos.durability.kind = DurabilityQosPolicyKind.from_int(0);
    localDataReaderQos.deadline = new DeadlineQosPolicy();
    localDataReaderQos.deadline.period = new Duration_t();
    localDataReaderQos.latency_budget = new LatencyBudgetQosPolicy();
    localDataReaderQos.latency_budget.duration = new Duration_t();
    localDataReaderQos.liveliness = new LivelinessQosPolicy();
    localDataReaderQos.liveliness.kind = LivelinessQosPolicyKind.from_int(0);
    localDataReaderQos.liveliness.lease_duration = new Duration_t();
    localDataReaderQos.reliability = new ReliabilityQosPolicy();
    localDataReaderQos.reliability.kind = ReliabilityQosPolicyKind.from_int(0);
    localDataReaderQos.reliability.max_blocking_time = new Duration_t();
    localDataReaderQos.destination_order = new DestinationOrderQosPolicy();
    localDataReaderQos.destination_order.kind = DestinationOrderQosPolicyKind.from_int(0);
    localDataReaderQos.history = new HistoryQosPolicy();
    localDataReaderQos.history.kind = HistoryQosPolicyKind.from_int(0);
    localDataReaderQos.resource_limits = new ResourceLimitsQosPolicy();
    localDataReaderQos.user_data = new UserDataQosPolicy();
    localDataReaderQos.user_data.value = new byte[0];
    localDataReaderQos.ownership = new OwnershipQosPolicy();
    localDataReaderQos.ownership.kind = OwnershipQosPolicyKind.from_int(0);
    localDataReaderQos.time_based_filter = new TimeBasedFilterQosPolicy();
    localDataReaderQos.time_based_filter.minimum_separation = new Duration_t();
    localDataReaderQos.reader_data_lifecycle = new ReaderDataLifecycleQosPolicy();
    localDataReaderQos.reader_data_lifecycle.autopurge_nowriter_samples_delay = new Duration_t();
    localDataReaderQos.reader_data_lifecycle.autopurge_disposed_samples_delay = new Duration_t();
    
    DataReaderQosHolder localDataReaderQosHolder = new DataReaderQosHolder(localDataReaderQos);
    localSubscriber.get_default_datareader_qos(localDataReaderQosHolder);
    if (reliable) {
      localDataReaderQosHolder.value.reliability.kind = ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS;
    }
    localDataReaderQosHolder.value.history.kind = HistoryQosPolicyKind.KEEP_ALL_HISTORY_QOS;
    
    DataReaderListenerImpl localDataReaderListenerImpl = new DataReaderListenerImpl();
    DataReader localDataReader = localSubscriber.create_datareader(localTopic, localDataReaderQosHolder.value, localDataReaderListenerImpl, -1);
    if (localDataReader == null)
    {
      System.err.println("ERROR: DataReader creation failed");
      return;
    }
    StatusCondition localStatusCondition = localDataReader.get_statuscondition();
    localStatusCondition.set_enabled_statuses(16384);
    WaitSet localWaitSet = new WaitSet();
    localWaitSet.attach_condition(localStatusCondition);
    SubscriptionMatchedStatusHolder localSubscriptionMatchedStatusHolder = new SubscriptionMatchedStatusHolder(new SubscriptionMatchedStatus());
    Duration_t localDuration_t = new Duration_t(Integer.MAX_VALUE, Integer.MAX_VALUE);
    
    int i = 0;
    for (;;)
    {
      int j = localDataReader.get_subscription_matched_status(localSubscriptionMatchedStatusHolder);
      if (j != 0)
      {
        System.err.println("ERROR: get_subscription_matched_status()failed.");
        return;
      }
      if ((localSubscriptionMatchedStatusHolder.value.current_count == 0) && (localSubscriptionMatchedStatusHolder.value.total_count > 0))
      {
        System.out.println("Subscriber No Longer Matched");
        break;
      }
      if ((localSubscriptionMatchedStatusHolder.value.current_count > 0) && (i == 0))
      {
        System.out.println("Subscriber Matched");
        i = 1;
      }
      ConditionSeqHolder localConditionSeqHolder = new ConditionSeqHolder(new Condition[0]);
      if (localWaitSet.wait(localConditionSeqHolder, localDuration_t) != 0)
      {
        System.err.println("ERROR: wait() failed.");
        return;
      }
    }
    System.out.println("Subscriber Report Validity");
    localDataReaderListenerImpl.report_validity();
    
    localWaitSet.detach_condition(localStatusCondition);
    
    System.out.println("Stop Subscriber");
    
    localDomainParticipant.delete_contained_entities();
    localDomainParticipantFactory.delete_participant(localDomainParticipant);
    TheServiceParticipant.shutdown();
    
    System.out.println("Subscriber exiting");
  }
}

