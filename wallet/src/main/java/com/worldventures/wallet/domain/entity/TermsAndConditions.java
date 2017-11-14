package com.worldventures.wallet.domain.entity;


import org.immutables.value.Value;

@Value.Immutable
public interface TermsAndConditions {

   String url();

   String tacVersion();
}
