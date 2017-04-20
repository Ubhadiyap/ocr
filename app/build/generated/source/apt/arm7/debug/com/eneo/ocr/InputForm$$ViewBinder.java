// Generated code from Butter Knife. Do not modify!
package com.eneo.ocr;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class InputForm$$ViewBinder<T extends com.eneo.ocr.InputForm> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558585, "field '_serial'");
    target._serial = finder.castView(view, 2131558585, "field '_serial'");
    view = finder.findRequiredView(source, 2131558586, "field '_index'");
    target._index = finder.castView(view, 2131558586, "field '_index'");
    view = finder.findRequiredView(source, 2131558583, "field '_long_lat'");
    target._long_lat = finder.castView(view, 2131558583, "field '_long_lat'");
    view = finder.findRequiredView(source, 2131558584, "field '_date'");
    target._date = finder.castView(view, 2131558584, "field '_date'");
    view = finder.findRequiredView(source, 2131558587, "field '_island'");
    target._island = finder.castView(view, 2131558587, "field '_island'");
    view = finder.findRequiredView(source, 2131558588, "field '_zone'");
    target._zone = finder.castView(view, 2131558588, "field '_zone'");
    view = finder.findRequiredView(source, 2131558589, "field '_agency'");
    target._agency = finder.castView(view, 2131558589, "field '_agency'");
    view = finder.findRequiredView(source, 2131558590, "field '_agencyID'");
    target._agencyID = finder.castView(view, 2131558590, "field '_agencyID'");
    view = finder.findRequiredView(source, 2131558591, "field '_saveButton'");
    target._saveButton = finder.castView(view, 2131558591, "field '_saveButton'");
  }

  @Override public void unbind(T target) {
    target._serial = null;
    target._index = null;
    target._long_lat = null;
    target._date = null;
    target._island = null;
    target._zone = null;
    target._agency = null;
    target._agencyID = null;
    target._saveButton = null;
  }
}
